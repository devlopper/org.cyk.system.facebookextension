package org.cyk.system.facebookextension.tools.collector;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.cyk.system.facebookextension.model.Tag;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Group implements Serializable {
	
	private static final long serialVersionUID = -6376789573256524964L;
	private long id;//The ID of the user, page, group, or event whose wall the post is on
	private String name;
	private Set<Tag> tags = new HashSet<Tag>();
	private int createdPostCount;
	private int postCount;
	
	public Group(long id,String name,Tag...tags) {
		this.id = id;
		this.name = name;
		if(tags!=null){
			this.tags.addAll(Arrays.asList(tags));
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Group other = (Group) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	/*-------------------------------------*/
	
	//these are the groups where I fetch my data
	public static final Set<Group> SOURCE = new LinkedHashSet<Group>();
	
	public static final Set<Group> SOURCE_SELL_BUY = new LinkedHashSet<Group>();
	
	static {
		createGroup(SOURCE_SELL_BUY, 286124794747358L,"CENTRE COMMERCIAL DAVID BIEFFO", Tag.SELL_BUY);
		createGroup(SOURCE_SELL_BUY, 439542059419422L,"SMART-PHONE MARKET", Tag.SELL_BUY);
		createGroup(SOURCE_SELL_BUY, 209174175882512L,"ABIDJAN TROQUE et Echange", Tag.SELL_BUY);
		createGroup(SOURCE_SELL_BUY, 415700018485696L,"ABIDJAN DJASSA CLUB", Tag.SELL_BUY);
		createGroup(SOURCE_SELL_BUY, 261467323941929L,"Blackmarket de babi", Tag.SELL_BUY);
		createGroup(SOURCE_SELL_BUY, 350880938339514L,"AbIdJaNbIzZ", Tag.SELL_BUY);
		createGroup(SOURCE_SELL_BUY, 366051806797471L,"New Black Market", Tag.SELL_BUY);
		createGroup(SOURCE_SELL_BUY, 386775561380484L,"ABIDJAN CENTER", Tag.SELL_BUY);
		createGroup(SOURCE_SELL_BUY, 437338216298659L,"ZOOM-MARKET.NET", Tag.SELL_BUY);
		createGroup(SOURCE_SELL_BUY, 498487570178425L,"LA RUE COMMERCIALE DE BABY", Tag.SELL_BUY);
		createGroup(SOURCE_SELL_BUY, 312649346435L,"LE BLACK MARKET DE FACEBOOK", Tag.SELL_BUY);
		createGroup(SOURCE_SELL_BUY, 196071440407513L,"BLACK MARKET (Ventes & Achats 2 tt genre)", Tag.SELL_BUY);
		createGroup(SOURCE_SELL_BUY, 223001994432752L,"DJASSA DALOA", Tag.SELL_BUY);
		createGroup(SOURCE_SELL_BUY, 179603642132367L,"BONNES AFFAIRES ABIDJAN", Tag.SELL_BUY);
		createGroup(SOURCE_SELL_BUY, 243458532417723L,"ABIDJAN-CHIC", Tag.SELL_BUY);
		createGroup(SOURCE_SELL_BUY, 294997447277821L,"LE DJASSA - COCODY", Tag.SELL_BUY);
		createGroup(SOURCE_SELL_BUY, 226914860681781L,"J'achète et je vends", Tag.SELL_BUY);
		createGroup(SOURCE_SELL_BUY, 191665344204319L,"Business Room", Tag.SELL_BUY);
		createGroup(SOURCE_SELL_BUY, 280816311930240L,"Troovtou", Tag.SELL_BUY);
		createGroup(SOURCE_SELL_BUY, 249766575044461L,"Le Djassa Virtuel - www.djassavirtuel.net", Tag.SELL_BUY);
		createGroup(SOURCE_SELL_BUY, 258908824119838L,"Djassa France Inter", Tag.SELL_BUY);
		createGroup(SOURCE_SELL_BUY, 128748623807594L,"Djassa Business Center", Tag.SELL_BUY);
		createGroup(SOURCE_SELL_BUY, 122093447897293L,"Comme au DJASSA", Tag.SELL_BUY);
		createGroup(SOURCE_SELL_BUY, 334319943324037L,"Au Black Quoi (Au Djassa Quoi !!!)", Tag.SELL_BUY);
		createGroup(SOURCE_SELL_BUY, 441777105867716L,"Au DjêKonan Quoi (Au Djassa Quoi)", Tag.SELL_BUY);
		createGroup(SOURCE_SELL_BUY, 140529672757017L,"Fan Club Opérateur Djassacité", Tag.SELL_BUY);
		createGroup(SOURCE_SELL_BUY, 276716272357886L,"LE DJASSA", Tag.SELL_BUY);
		createGroup(SOURCE_SELL_BUY, 218007948259080L,"BLACK MARKET (Ventes & Achats 2 tt genre) by TAC", Tag.SELL_BUY);
		createGroup(SOURCE_SELL_BUY, 243832138990592L,"DJASSA EN LIGNE", Tag.SELL_BUY);
		createGroup(SOURCE_SELL_BUY, 187745941281706L,"Le djassa virtuel", Tag.SELL_BUY);
		createGroup(SOURCE_SELL_BUY, 315968121760530L,"Black Market virtuel ( BMV)", Tag.SELL_BUY);
		createGroup(SOURCE_SELL_BUY, 200863556633781L,"Djassa", Tag.SELL_BUY);
		createGroup(SOURCE_SELL_BUY, 132160590209519L,"BLACK MARKET", Tag.SELL_BUY);
		createGroup(SOURCE_SELL_BUY, 130812683673099L,"Au Djassa Quoi !!!", Tag.SELL_BUY);
		createGroup(SOURCE_SELL_BUY, 182094081864733L,"TREICHVILLE DJASSA", Tag.SELL_BUY);
		
		SOURCE.addAll(SOURCE_SELL_BUY);
	}
	
	//these are my groups
	//ONLY ONE AND ONE Group by Tag
	public static final Set<Group> DESTINATION_SELL_BUY = new LinkedHashSet<Group>();
	
	public static final Group SELL_BUY = createGroup(DESTINATION_SELL_BUY, 132346010261919L,"Test Sell Buy", Tag.SELL_BUY);
	
	static Group createGroup(Set<Group> groups,long id,String name,Tag...tags){
		Group group = new Group(id, name,tags);
		groups.add(group);
		return group;
	}
	

}
