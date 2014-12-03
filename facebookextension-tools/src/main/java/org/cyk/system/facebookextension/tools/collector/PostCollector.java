package org.cyk.system.facebookextension.tools.collector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.cyk.system.facebookextension.model.Post;
import org.cyk.utility.common.ThreadPoolExecutor;
import org.cyk.utility.common.cdi.AbstractBean;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.exception.FacebookOAuthException;

@Getter @Setter
public class PostCollector extends AbstractBean implements Runnable, Serializable,GroupPostCollectorListener {

	private static final long serialVersionUID = -2733931013865801179L;

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmm");
	
	private Collection<PostCollectorListener> postCollectorListeners = new ArrayList<>();

	private Application application;
	
	private Set<Group> groups = new LinkedHashSet<Group>();
	private Date fromCreationDate,toCreationDate;
	private Long limit,intervalOld=DateUtils.MILLIS_PER_DAY,intervalNew=DateUtils.MILLIS_PER_MINUTE*5,waitOld;
	private Boolean stop=Boolean.TRUE,actualMode=false;
	private Group mostPostedGroup;
	//outputs
	private List<Post> posts = new LinkedList<Post>();
	private Throwable authenticationException;
	private ThreadPoolExecutor threadPoolExecutor ;
	private File directory;
	
	@Override
	public void run() {
		authenticationException=null;
		__writeInfo__(this.getClass().getSimpleName()+" STARTS RUNNING...");
		for(PostCollectorListener postCollectorListener : postCollectorListeners)
			postCollectorListener.started(this);
		Date startCreationDate = fromCreationDate,endCreationDate=new Date(startCreationDate.getTime()+intervalOld);
		if(endCreationDate.getTime()-startCreationDate.getTime()<intervalNew)
			startCreationDate = new Date(endCreationDate.getTime()-intervalNew);
		FacebookClient facebookClient = new DefaultFacebookClient(application.getAccessToken());
		//application.setAccessTokenInfo(facebookClient.debugToken(application.getAccessToken()));
		//System.out.println(application.getAccessTokenInfo());
		while(!stopCondition(startCreationDate,endCreationDate)){
			long executionTimeMillis = System.currentTimeMillis();
			__writeInfo__("Reading post From : "+startCreationDate+"  TO  "+endCreationDate);
			posts.clear();
			threadPoolExecutor = new ThreadPoolExecutor(5, 10, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(50));
			for(Group group : groups)
				threadPoolExecutor.execute(new GroupPostCollector(this,facebookClient,group, startCreationDate, endCreationDate,limit));
			threadPoolExecutor.waitTermination(10, TimeUnit.MINUTES);
			
			if(authenticationException!=null){
				stop = Boolean.TRUE;
				stop();
				
				for(PostCollectorListener postCollectorListener : postCollectorListeners)
					postCollectorListener.authenticationException(this);
				continue;
			}else if(Boolean.TRUE.equals(stop)){
				stop();
				return;
			}
				
			__writeInfo__("Duration in second : "+(System.currentTimeMillis()-executionTimeMillis)/1000);
			if(posts.size()>0){
				__writeInfo__(posts.size()+" posts found");
				//__writeInfo__("Most Posted Group : "+mostPostedGroup.getName()+" - "+mostPostedGroup.getPostCount());
				Utils.clean(posts);	
				
				for(PostCollectorListener postCollectorListener : postCollectorListeners)
					postCollectorListener.processPosts(this);
				String fileName = DATE_FORMAT.format(startCreationDate)+" "+DATE_FORMAT.format(endCreationDate);
				try {
					SerializationUtils.serialize((Serializable) posts,new FileOutputStream(new File(directory, fileName+".txt")));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else
				__writeInfo__(this.getClass().getSimpleName()+" - NO DATA");
			
			if(!actualMode){
				startCreationDate=endCreationDate;
				endCreationDate=new Date(startCreationDate.getTime()+intervalOld);
				Date now =  new Date();
				if(endCreationDate.after(now))
					endCreationDate = now;
				if(waitOld!=null)
					try {
						//__writeInfo__("Wai__writeInfo__ in Old");
						Thread.sleep(waitOld);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			}
			
			if(actualMode || (actualMode = (endCreationDate.getTime()-startCreationDate.getTime()<intervalNew)) )
				try {
					__writeInfo__("Waiting in New");
					Thread.sleep(intervalNew);
					Date now =  new Date();
					startCreationDate = new Date(now.getTime()-intervalNew);
					endCreationDate = now;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
	}
	
	/**/
	
	private boolean stopCondition(Date start,Date end){
		if(stop)
			return true;//no choice , we must stop
		if(toCreationDate==null)//no limit fixed , continue
			return false;
		return start.after(toCreationDate);
	}
	
	private void stop(){
		for(PostCollectorListener postCollectorListener : postCollectorListeners)
			postCollectorListener.stopped(this);
	}

	/**/
	
	@Override
	public void createQuery(GroupPostCollector groupPostCollector) {
		
	}
	
	@Override
	public void exceptionWhileExecutingQuery(GroupPostCollector groupPostCollector) {
		if(groupPostCollector.getThrowable() instanceof FacebookOAuthException){
			authenticationException = groupPostCollector.getThrowable();
		}
	}

	@Override
	public void processResults(GroupPostCollector groupPostCollector) {
		
	}

	@Override
	public void processPosts(GroupPostCollector groupPostCollector) {
		posts.addAll(groupPostCollector.getPosts());
	}
	
	/**/
	
	

	/**/
	
	public static void main(String[] args) throws ParseException {
		Application application = new Application("147625215390534", "108693eea0a8c3b78e5d4819ff58c51c",AbstractCollector.TEST_AT);
		PostCollector collector = new PostCollector();
		collector.setApplication(application);
		collector.setGroups(Group.SOURCE_SELL_BUY);
		collector.setFromCreationDate(DateUtils.parseDate("01/06/2014", "dd/MM/yyyy"));
		collector.setToCreationDate(null);
		collector.getPostCollectorListeners().add(new PostCollectorListener() {
			
			@Override
			public void processPosts(PostCollector postCollector) {
				
			}
			
			@Override
			public void authenticationException(PostCollector postCollector) {
				
			}

			@Override
			public void stopped(PostCollector postCollector) {
				
			}

			@Override
			public void started(PostCollector postCollector) {
				
			}
		});
		collector.run();
	}
	
}
