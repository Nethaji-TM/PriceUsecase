function fetchPricing() {
	const storeId = document.getElementById("storeId").value;
	const artifactId = document.getElementById("artifactId").value;

	if (!storeId || !artifactId) {
		alert("Please enter both Store ID and Artifact ID.");
		return;
	}
	const apiUrl = `http://localhost:8080/api/pricing/v1/prices/${storeId}/${artifactId}?page=1&pageSize=3`;
	fetch(apiUrl)
		.then(response => response.json())
		.then(data => {
			alert("1");
			if (data && Object.keys(data).length > 0) {
				document.getElementById("responseContainer").style.display = "block";
				displayResponse(data, storeId, artifactId);
			}
		})
		.catch(error => console.error("Error fetching data:", error));
}

function displayResponse(data, storeId, artifactId) {
	alert("2");
	let responseTable = document.getElementById("responseTableBody");
	responseTable.innerHTML = ""; // Clear previous data

	data.prices.forEach(price => {
                const row = `<tr>
                    <td>${price.storeID}</td>
                    <td>${price.articleID}</td>
                    <td>${price.type}</td>
                    <td>${price.subtype}</td>
                    <td>${price.amount} ${price.currency}</td>
                    <td>${new Date(price.validFrom).toLocaleDateString()}</td>
                    <td>${new Date(price.validTo).toLocaleDateString()}</td>
                    <td>${price.overlapped ? "Yes" : "No"}</td>
                </tr>`;
		responseTable.innerHTML += row;
	});
}