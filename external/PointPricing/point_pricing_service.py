from flask import Flask
from flask_restful import Resource, Api, abort, reqparse

app = Flask(__name__)
api = Api(app)

USERS = {
    'Stephane': {'points': 4},
    'Jerome': {'points': 10}
}


def abort_if_user_doesnt_exist(user_name):
    if user_name not in USERS:
        abort(404, message="User {} doesn't exist".format(user_name))


parser = reqparse.RequestParser()
parser.add_argument('points')


class User(Resource):
    def get(self, user_name):
        abort_if_user_doesnt_exist(user_name)
        return USERS[user_name]

    def put(self, user_name):
        args = parser.parse_args()
        USERS[user_name]["points"] = int(args["points"])
        return USERS[user_name], 201


class Users(Resource):
    def get(self):
        return USERS


api.add_resource(User, '/user/<string:user_name>')
api.add_resource(Users, '/users')

if __name__ == '__main__':
    app.run(debug=True)
