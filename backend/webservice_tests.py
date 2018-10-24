import requests

rpc_id = 0

def rpc_call(url, method, params):
    global rpc_id
    rpc_id += 1
    res = requests.post(url, json={"jsonrpc": "2.0", "method": method, "id": rpc_id, "params": params})
    response_object = res.json()
    print(response_object)
    if "error" in response_object:
        print("Error: " + response_object["error"])
        return None
    return response_object["result"]
        
print(rpc_call("http://localhost:8080/notification", "pullNotificationForUser", {"username": "Jeremy"}))