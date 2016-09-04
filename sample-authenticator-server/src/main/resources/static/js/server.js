function parseHash() {
	var hash = window.location.hash;
	if (!hash) {
		return false;
	}
	
	var params = {};
	var split = hash.substring(1).split("&");
	for (i in split) {
		var pair = split[i].split('=');
		params[pair[0]] = decodeURIComponent(pair[1]);
	}
	
	return params;
}


function appendHash(id) {
	var params = parseHash();
	if (!params) {
		return;
	}
	
	var table = document.getElementById(id);
	
	for (i in params) {
		var tr = document.createElement('tr');
		var td1 = document.createElement('td'); 
		var td2 = document.createElement('td');
		td1.innerHTML = i;
		td2.innerHTML = params[i];
		tr.appendChild(td1);
		tr.appendChild(td2);
		table.appendChild(tr);
	}
}