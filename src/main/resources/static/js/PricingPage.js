import React, { useState } from "react";

const PricingPage = () => {
  const [storeId, setStoreId] = useState("");
  const [artifactId, setArtifactId] = useState("");
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const response = await fetch(
        `http://localhost:8080/api/pricing?storeId=${storeId}&artifactId=${artifactId}`
      );
      
      if (!response.ok) {
        throw new Error("Failed to fetch data");
      }
      
      const result = await response.json();
      setData(result);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mx-auto p-4">
      <h2 className="text-xl font-bold mb-4">Pricing Lookup</h2>
      <form onSubmit={handleSubmit} className="mb-4 space-y-2">
        <div>
          <label className="mr-2">Store ID:</label>
          <input
            type="text"
            value={storeId}
            onChange={(e) => setStoreId(e.target.value)}
            className="border p-1"
            required
          />
        </div>
        <div>
          <label className="mr-2">Artifact ID:</label>
          <input
            type="text"
            value={artifactId}
            onChange={(e) => setArtifactId(e.target.value)}
            className="border p-1"
            required
          />
        </div>
        <button
          type="submit"
          className="bg-blue-500 text-white px-4 py-2 rounded"
          disabled={loading}
        >
          {loading ? "Loading..." : "Submit"}
        </button>
      </form>
      {error && <p className="text-red-500">Error: {error}</p>}
      {data && (
        <table className="border-collapse border border-gray-400 w-full mt-4">
          <thead>
            <tr>
              <th className="border border-gray-400 p-2">Price</th>
              <th className="border border-gray-400 p-2">Valid Date</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td className="border border-gray-400 p-2">{data.price}</td>
              <td className="border border-gray-400 p-2">{data.validDate}</td>
            </tr>
          </tbody>
        </table>
      )}
    </div>
  );
};

export default PricingPage;
