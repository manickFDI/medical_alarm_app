from flask import Flask
from flask.ext.mysql import MySQL

app = Flask(__name__)

mysql = MySQL()

# MySQL configurations
app.config['MYSQL_DATABASE_USER'] = 'root'
app.config['MYSQL_DATABASE_PASSWORD'] = 'rooting'
app.config['MYSQL_DATABASE_DB'] = 'test'
app.config['MYSQL_DATABASE_HOST'] = 'localhost'
mysql.init_app(app)


def showUsers():
    conn = mysql.connect()
    cur = conn.cursor()
    query = "select * from user"
    # Execute main SQL Statement
    cur.execute(query)
    # Process the response.
    # Just one row is expected
    rows = cur.fetchall()
    # Build the return object
    users = ""
    for row in rows:
        users = users + " " + row[0]
    mysql.close()
    return users


@app.route("/")
def main():
    return showUsers()


if __name__ == "__main__":
    app.run()
