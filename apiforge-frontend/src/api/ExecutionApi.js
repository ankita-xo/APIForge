const API_BASE_URL = "http://localhost:8080";
export async function executeRequest(request){
    const response = await fetch(API_BASE_URL + "/api/executions",{
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(request)
    })
    return response.json();
}