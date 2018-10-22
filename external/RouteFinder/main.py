from flask import Flask, jsonify

app = Flask(__name__)


@app.route('/find_route/<start>/<end>')
def get(start, end):
    drivers = [{
        "name": "Joseph",
        "from": start,
        "to": end
    }]
    return jsonify({"drivers": drivers})


if __name__ == '__main__':
    app.run(host='0.0.0.0', debug=True, port=8000)  # Used just for dev, default docker image port is 80
