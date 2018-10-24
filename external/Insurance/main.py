from flask import Flask, jsonify
import random

app = Flask(__name__)


@app.route('/check_insurance/<id>')
def get(id):
    result = True
    if random.random() > 0.5:
        result = False
    return jsonify({"insuranceInvolvement": result})


if __name__ == '__main__':
    app.run(host='0.0.0.0', debug=True, port=8000)