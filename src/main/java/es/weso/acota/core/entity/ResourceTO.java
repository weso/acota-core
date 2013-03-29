package es.weso.acota.core.entity;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Contains data related to a Resource, as URI, label, description
 * or type
 * @author Jose María Álvarez
 *
 */
public class ResourceTO {

	public final static int DEFAULT = 0;
	public final static int UNESCAPING_HTML = 1;
	
	private String uri;
	private String label;
	private String description;
	private String type;
	private int mode;

	/**
	 * Zero-argument default constructor.
	 */
	public ResourceTO() {
		super();
		this.uri = "";
		this.label = "";
		this.description = "";
		this.type = "";
		this.mode = DEFAULT;
	}
	

	/**
	 * One-argument secondary constructor.
	 * @param mode Special Characters Escaping Mode
	 */
	public ResourceTO(int mode) {
		super();
		this.uri = "";
		this.label = "";
		this.description = "";
		this.type = "";
		this.mode = mode;
	}
	
	/**
	 * Secondary constructor.
	 * @param uri Resource's URI
	 * @param label Resource's label
	 * @param description Resource's description
	 * @param type Resource's type
	 * @param mode Special Characters Escaping Mode
	 */
	public ResourceTO(String uri, String label, String description, String type, int mode) {
		super();
		this.uri = uri;
		this.label = label;
		this.description = description;
		this.type = type;
		this.mode = mode;
	}
	
	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = unescapeText(label);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = unescapeText(description);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getMode() {
		return mode;
	}

	private String unescapeText(String text) {
		if(mode==ResourceTO.UNESCAPING_HTML){
			return StringEscapeUtils.unescapeHtml4(text);
		}else{
			return text;
		}
	}

	@Override
	public String toString() {
		return "ResourceTO [uri=" + uri + ", label=" + label + ", description="
				+ description + ", type=" + type + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
		ResourceTO other = (ResourceTO) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}

}
