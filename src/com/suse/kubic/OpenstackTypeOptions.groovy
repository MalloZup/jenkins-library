package com.suse.kubic;

class OpenstackTypeOptions implements Serializable {
	String image = null;

	String adminFlavor = 'm1.large';
	String masterFlavor = 'm1.large';
	String workerFlavor = 'm1.large';
}
