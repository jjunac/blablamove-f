from flask import Flask, jsonify, request

app = Flask(__name__)


@app.route('/find_driver')
def get():
    start_lat = request.args["start_lat"]
    start_long = request.args["start_long"]
    end_lat = request.args["end_lat"]
    end_long = request.args["end_long"]
    drivers = [{
        "name": "Erick",
        "from": f'{start_lat},{start_long}',
        "to": f'{end_lat},{end_long}'
    }]
    print(f"returning {drivers}")
    return jsonify({"drivers": drivers})


if __name__ == '__main__':
    app.run(host='0.0.0.0', debug=True, port=8000)  # Used just for dev
