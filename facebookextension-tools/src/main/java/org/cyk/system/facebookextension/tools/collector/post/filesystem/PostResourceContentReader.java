package org.cyk.system.facebookextension.tools.collector.post.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.cyk.system.facebookextension.model.Post;
import org.cyk.system.facebookextension.tools.collector.Application;
import org.cyk.system.facebookextension.tools.collector.Group;
import org.cyk.system.facebookextension.tools.collector.post.PostReader;

/**
 * Persist post on file
 * @author Christian
 *
 */
@Getter @Setter
public class PostResourceContentReader extends PostReader implements Serializable {

	private static final long serialVersionUID = 7632924615002464328L;

	static final String[] IMAGE_EXTENSION = {"png","jpg","jpeg"};
	
	public static final File OUTPUT_FOLDER = new File("D:\\Desktop\\Facebook Posts");
	
	static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy HH-mm", Locale.FRENCH);
	
	protected String me;
	private File folder;
	private List<PostFileRecord> list = new LinkedList<PostFileRecord>();
	protected boolean autoSaveToFile=true;
	
	public PostResourceContentReader(Application application, Date fromCreationDate,Date toCreationDate,String me,File folder) {
		super(application, fromCreationDate, toCreationDate);
		this.me = me;
		this.folder = folder;
	}

	public PostResourceContentReader(Application application, Set<Group> groups,Date fromCreationDate, Date toCreationDate,String me,File folder) {
		super(application, groups, fromCreationDate, toCreationDate);
		this.me = me;
		this.folder = folder;
	}

	public PostResourceContentReader(Application application,String me,File folder) {
		super(application);
		this.me = me;
		this.folder = folder;
	}
	
	public PostFileRecord getPostFileRecord(Post post){
		for(PostFileRecord p : list)
			if(p.getPost().getReference().equals(post.getReference()))
				return p;
		return null;
	}
	
	public static void save(final List<PostFileRecord> list,final File file){
		
		new Thread(){public void run() {
			try {
				System.out.println(list.size()+" PostFileRecord");
				SerializationUtils.serialize((Serializable) list,new FileOutputStream(file));
				System.out.println("Save to file done : "+file.getName());
			} catch (FileNotFoundException e) {
				System.out.println(e);
			}
		};}.start();	
	}
	
	@SuppressWarnings("unchecked")
	public static List<PostFileRecord> load(File file){
		try {
			return (List<PostFileRecord>) SerializationUtils.deserialize(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			System.out.println(e);
			return null;
		}
	}

	@Override
	protected final void consume(List<Post> posts,Date start,Date end) {
		long executionTimeMillis = System.currentTimeMillis();
		MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
	    HttpClient httpClient = new HttpClient(connectionManager);
	    list.clear();
		verbose("Downloading media data...");
		//use multi thread
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(150));
		for(Post post : posts)
			threadPoolExecutor.execute(new PostProcessor(post,httpClient));
		try {
			threadPoolExecutor.awaitTermination(10, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		verbose("Duration in second : "+(System.currentTimeMillis()-executionTimeMillis)/1000);
		if(autoSaveToFile){	
			verbose("Exporting...");
			String fileName = "post "+DATE_FORMATTER.format(start)+"  -TO-  "+DATE_FORMATTER.format(end)+" --- "+list.size()+".posts";
			save(list,new File(OUTPUT_FOLDER, fileName));
		}
		consumeRecord(list);
	}
	
	protected void consumeRecord(List<PostFileRecord> postFileRecords) {}
	
	/*
	private boolean isImage(String url){
		for(String extension : IMAGE_EXTENSION)
			if(url.endsWith("."+extension))
				return true;
		return false;
	}	
	*/
	public static void main(String[] args) {
		Application application = new Application("317227825044284", "adcec35241e9398a8a5dc919235152a4",TEST_AT);
		PostResourceContentReader exporter = new PostResourceContentReader(application,"132346010261919",OUTPUT_FOLDER);//
		try {
			exporter.setFromCreationDate(DateUtils.parseDate("01/01/2013", "dd/MM/yyyy"));
			exporter.setToCreationDate(DateUtils.parseDate("01/13/2013", "dd/MM/yyyy"));
		} catch (ParseException e) {
		}
		//publisher.setFromCreationDate(new Date());
		//publisher.setIntervalOld(com.kyc.utils.DateUtils.MINUTE*3);
		exporter.setVerbose(true);
		//publisher.setLimit(1L);
		exporter.run();
		
		//importData(publisher);
	}
	
	private class PostProcessor implements Runnable{
		Post post;
		HttpClient httpClient;

		public PostProcessor(Post post,HttpClient httpClient) {
			super();
			this.post = post;
			this.httpClient = httpClient;
		}
		
		public void run() {
			Media media = null;
			try {
				//media = new Media(post.getMediaUrl(), me,httpClient);
			} catch (Exception e) {
				if(e instanceof javax.net.ssl.SSLPeerUnverifiedException)
					;
				else
					;//System.err.println("Error while downloading media "+post.getMediaUrl()+" - "+e);
			}
			synchronized (PostResourceContentReader.class) {
				list.add(new PostFileRecord(post,media.getFacebookConnection() ,media==null?null:media.getContent()));
			}
			
		}
	}
	
}
