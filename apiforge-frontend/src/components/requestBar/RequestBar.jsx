import { useState } from 'react'
function RequestBar({onSend}) {
    const [method, setMethod] = useState("GET");
    const [url, setUrl] = useState("");

    return (
        <>
        <select value={method} onChange={onChange => setMethod(onChange.target.value)}>
    <option value="GET">GET</option>
    <option value="POST">POST</option>
    <option value="PUT">PUT</option>
    <option value="PATCH">PATCH</option>
    <option value="DELETE">DELETE</option>
    </select >
    <input type ="URL" value={url} placeholder="Enter URL" onChange= {e => setUrl(e.target.value)}/>
    <button onClick={() => onSend({method,
    url,
    requestBody: null,
    queryParams: {},
    headers: {}
})}
    >Send</button>
</>
    )
}

export default RequestBar;