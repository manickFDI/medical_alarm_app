from flask import Flask
from flask.ext.pymongo import PyMongo

app = Flask(__name__)
app.config['MONGO_DBNAME'] = 'test'
mongo = PyMongo(app)


def showUsers():
    cur = mongo.db.user.find()
    users = ""
    for item in cur:
        users = users + " " + item['name']
    return users


@app.route("/")
def main():
    return showUsers()


if __name__ == "__main__":
    app.run()