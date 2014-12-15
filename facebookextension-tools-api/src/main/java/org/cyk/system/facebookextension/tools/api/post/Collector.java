package org.cyk.system.facebookextension.tools.api.post;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.cyk.system.facebookextension.model.Constants;
import org.cyk.system.facebookextension.model.Post;
import org.cyk.system.facebookextension.tools.provider.restfb.RestFB;
import org.cyk.system.root.model.event.Period;
import org.cyk.utility.common.RunnableAdapter;
import org.cyk.utility.common.RunnableListener;
import org.cyk.utility.common.cdi.AbstractBean;
import org.cyk.utility.common.cdi.BeanAdapter;

@Getter @Setter
public class Collector extends AbstractBean implements Runnable, Serializable {

	private static final long serialVersionUID = -2733931013865801179L;

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmm");
	
	private Collection<CollectorListener> collectorListeners = new ArrayList<>();
	private Collection<RunnableListener<Collector>> runnableListeners = new ArrayList<>();
	
	private Period period = new Period(),creation=new Period();
	private Date startCreationDate,endCreationDate;
	
	private String tagId;
	private Set<Group> groups;
	
	private Long limit=2000l,intervalOld=DateUtils.MILLIS_PER_DAY,intervalNew=DateUtils.MILLIS_PER_MINUTE*5,waitOld;
	private Boolean stop=Boolean.TRUE,actualMode=false;
	private Group mostPostedGroup;
	//outputs
	private List<Post> posts = new LinkedList<Post>();
	//private Throwable authenticationException;
	//private ThreadPoolExecutor threadPoolExecutor ;
	private File directory;
	
	public Collector(File directory,String postTagId) {
		this.directory = directory;
		groups = Group.collection(postTagId, Boolean.TRUE);
		this.tagId = postTagId;
	}
	
	@Override
	public void run() {
		//authenticationException=null;
		__writeInfo__(this.getClass().getSimpleName()+" [Group count:"+groups.size()+"] starts...");
		runnableStarted(this, runnableListeners);
		startCreationDate = creation.getFromDate();
		endCreationDate=new Date(startCreationDate.getTime()+intervalOld);
		if(endCreationDate.getTime()-startCreationDate.getTime()<intervalNew)
			startCreationDate = new Date(endCreationDate.getTime()-intervalNew);
		//application.setAccessTokenInfo(facebookClient.debugToken(application.getAccessToken()));
		//System.out.println(application.getAccessTokenInfo());
		while(!stopCondition(startCreationDate,endCreationDate)){
			long executionTimeMillis = System.currentTimeMillis();
			__writeInfo__("Reading post From : "+startCreationDate+"  TO  "+endCreationDate);
			posts.clear();
			/*
			threadPoolExecutor = new ThreadPoolExecutor(5, 10, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(50));
			for(Group group : groups)
				threadPoolExecutor.execute(new GroupPostCollector(this,facebookClient,group, startCreationDate, endCreationDate,limit));
			threadPoolExecutor.waitTermination(10, TimeUnit.MINUTES);
			*/
			//Batch request
			
			for(CollectorListener listener : collectorListeners)
				try {
					listener.fetch(this);
				} catch (Exception exception) {
					stop = Boolean.TRUE;
					runnableStopped(this, runnableListeners);
					runnableThrowable(this, runnableListeners, exception);
				}
			
			if(Boolean.TRUE.equals(stop)){
				runnableStopped(this, runnableListeners);
				return;
			}
				
			__writeInfo__("Duration in second : "+(System.currentTimeMillis()-executionTimeMillis)/1000);
			if(posts.size()>0){
				__writeInfo__(posts.size()+" posts found");
				//__writeInfo__("Most Posted Group : "+mostPostedGroup.getName()+" - "+mostPostedGroup.getPostCount());
				clean(posts);	
				
				String fileName = DATE_FORMAT.format(startCreationDate)+" "+DATE_FORMAT.format(endCreationDate)+" "+posts.size()+"."+PostFileNameFilter.EXTENSION;
				Utils.writePosts(new File(directory, fileName), posts, beanListeners);
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
		if(creation.getToDate()==null)//no limit fixed , continue
			return false;
		return start.after(creation.getToDate());
	}
	
	private static void clean(List<Post> posts){
		//System.out.println(posts.size()+" Posts before removing no message and media");
		for(int i=0;i<posts.size();)
			if( StringUtils.isEmpty(posts.get(i).getMessage()) && posts.get(i).getMedia()==null)
				posts.remove(i);
			else
				i++;
		//System.out.println(posts.size()+" Posts after removing no message and media");
		List<Post> temp = new LinkedList<Post>(posts);
		posts.clear();
		Set<String> messages = new HashSet<String>();
		//System.out.println(temp.size()+" Posts before removing duplicate");
		for(int i=0;i<temp.size();)
			if(messages.add(temp.get(i).getMessage().toLowerCase()))
				posts.add(temp.remove(i));
			else
				i++;
		//System.out.println(posts.size()+" Posts after removing duplicate");
	}
	/**/
	
	
	/**/
	

	/**/

	public static void main(String[] args) throws Exception {
		Group.loadGroups();
		Collector collector = new Collector(new File("H:/fbposts"),Constants.TAG_POST_SELL);
		collector.getCreation().setFromDate(DateUtils.parseDate("03/06/2014", "dd/MM/yyyy"));
		
		collector.getCollectorListeners().add(new RestFB());
		
		collector.getRunnableListeners().add(new RunnableAdapter<Collector>(){
			@Override
			public void throwable(Collector aRunnable, Throwable throwable) {
				throwable.printStackTrace();
			}
		});
		collector.getBeanListeners().add(new BeanAdapter(){
			@Override
			public void info(String message) {
				System.out.println(message);
			}
		});
		collector.setStop(Boolean.FALSE);
		collector.run();
	}

}
