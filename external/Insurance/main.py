from flask import Flask, jsonify
import random

app = Flask(__name__)

users_who_asked_for_insurance = set()


@app.route('/insurance/<id>')
def get_cover(id):
    print(f"{id} asked for insurance protection")
    users_who_asked_for_insurance.add(id)
    return jsonify({"insuranceInvolvement": (id == "Johann")})


@app.route('/insurances/<id>')
def asked_insurance(id):
    print(f"Checking if {id} already asked for insurance protection")
    return jsonify({"requestedInsurance": (id in users_who_asked_for_insurance)})


if __name__ == '__main__':
    app.run(host='localhost', debug=True, port=5000)
