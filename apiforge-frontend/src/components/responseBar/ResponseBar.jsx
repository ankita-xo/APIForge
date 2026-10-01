function ResponsePanel({response}) {

    return (
    <>
    <div><h2>Response</h2></div>
    <div>{response ? JSON.stringify(response) : "No response yet."}</div>
    
    </>
)
}

export default ResponsePanel;