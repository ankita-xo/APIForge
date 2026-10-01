import { useState } from 'react'
import RequestBar from "../src/components/requestBar/RequestBar";
import ResponseBar from "../src/components/responseBar/ResponseBar";
import { executeRequest } from "../src/api/ExecutionApi";
import './App.css'

function App() {
  const [response, setResponse] = useState(null);

  async function handleExecute(request) {
    const result = await executeRequest(request);
    setResponse(result);
}

  return (
    <>
      <RequestBar onSend={handleExecute} />
      <ResponseBar response={response} />
    </>
  )
}

export default App
