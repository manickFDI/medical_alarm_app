from flask import Flask
from flask.ext.pymongo import PyMongo

app = Flask(__name__)
mongo = PyMongo(app)


@app.route("/")
def main():
    return showUsers()


if __name__ == "__main__":
    app.run()