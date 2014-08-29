package org.italiangrid.storm.webdav.authz;

import org.springframework.security.core.GrantedAuthority;


public class VOMSVOGrantedAuthority implements GrantedAuthority, Comparable<VOMSVOGrantedAuthority> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String voAuthority;
	
	public VOMSVOGrantedAuthority(String vo) {
		this.voAuthority = String.format("VO(%s)", vo);
	}

	@Override
	public String getAuthority() {
		return voAuthority;
	}

	@Override
	public int compareTo(VOMSVOGrantedAuthority that) {
		return voAuthority.compareTo(that.getAuthority());
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result
			+ ((voAuthority == null) ? 0 : voAuthority.hashCode());
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
		VOMSVOGrantedAuthority other = (VOMSVOGrantedAuthority) obj;
		if (voAuthority == null) {
			if (other.voAuthority != null)
				return false;
		} else if (!voAuthority.equals(other.voAuthority))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return getAuthority();
	}
}
