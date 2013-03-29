package es.weso.acota.core.entity;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Contains the results of the Enhancement
 * 
 * @author Jose María Álvarez
 */
public class SuggestionTO {

	protected Map<String, TagTO> tags;
	protected ResourceTO resource;

	/**
	 * Zero-argument default constructor.
	 */
	public SuggestionTO() {
		super();
		this.tags = new LinkedHashMap<String, TagTO>();
		this.resource = new ResourceTO();
	}

	/**
	 * One-argument constructor.
	 * @param resource Linked {@linked ResourceTO}
	 */
	public SuggestionTO(ResourceTO resource) {
		super();
		this.tags = new LinkedHashMap<String, TagTO>();
		this.resource = resource;
	}
	
	/**
	 * Two-argument constructor. 
	 * @param tags Map with label's name as keys and {@link TagTO} as values
	 * @param resource Linked {@linked ResourceTO}
	 */
	public SuggestionTO(Map<String, TagTO> tags, ResourceTO resource){
		super();
		this.tags = tags;
		this.resource = resource;
	}

	public Map<String, TagTO> getTags() {
		return tags;
	}

	public void setTags(Map<String, TagTO> tags) {
		this.tags = tags;
	}

	public ResourceTO getResource() {
		return resource;
	}

	public void setResource(ResourceTO resource) {
		this.resource = resource;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
		result = prime * result
				+ ((resource == null) ? 0 : resource.hashCode());
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
		SuggestionTO other = (SuggestionTO) obj;
		if (tags == null) {
			if (other.tags != null)
				return false;
		} else if (!tags.equals(other.tags))
			return false;
		if (resource == null) {
			if (other.resource != null)
				return false;
		} else if (!resource.equals(other.resource))
			return false;
		return true;
	}
	
}
