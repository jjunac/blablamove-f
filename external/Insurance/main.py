from flask import Flask, jsonify
import random

app = Flask(__name__)

users_who_asked_for_insurance = []

@app.route('/insurance/<id>')
def getCover(id):
    users_who_asked_for_insurance.append(id)
    return jsonify({"insuranceInvolvement": (id == "Jeremy")})

@app.route('/insurances/<id>')
def askedInsurance(id):
    return jsonify({"requestedInsurance": (id in users_who_asked_for_insurance)})

if __name__ == '__main__':
    app.run(host='localhost', debug=True, port=5000)