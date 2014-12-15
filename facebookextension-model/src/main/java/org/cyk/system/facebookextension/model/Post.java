package org.cyk.system.facebookextension.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

import org.cyk.system.root.model.AbstractIdentifiable;
import org.cyk.system.root.model.file.File;
import org.cyk.system.root.model.file.Tag;

@Entity
@Getter @Setter
public class Post extends AbstractIdentifiable implements Serializable {
	
	private static final long serialVersionUID = 8827008215003243466L;

	/**/
	
	
	/**/
	
	@Column(unique=true)
	private String reference;//reference the real post id on facebook database

	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;
	
	@Column(length=1024 * 6)
	private String message;
	
	@OneToOne(cascade=CascadeType.ALL)
	private File media;
	
	@OneToMany(fetch=FetchType.LAZY)
	private Set<Tag> tags = new HashSet<Tag>();
	
	public Post() {}
	
	/* short cut methods */
	/*
	public boolean isBigImageUrl(){
		if(mediaUrl==null)
			return false;
		return mediaUrl.endsWith("_n.jpg");
	}
	
	public String getSmallImageUrl(){
		return StringUtils.replace(mediaUrl, "_n.jpg", "_s.jpg");
	}
	
	public String getPermalink(){
		String[] ids = facebookId.split("_");
		return "http://www.facebook.com/"+ids[0]+"/posts/"+ids[1];
		//return "http://www.facebook.com/groups/"+ids[0]+"/permalink/"+ids[1];
	}
	*/
	
	
}
