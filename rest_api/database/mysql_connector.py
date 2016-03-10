from sqlalchemy import create_engine

MYSQL_DATABASE_USER = 'root'
MYSQL_DATABASE_PASSWORD = 'rooting'
MYSQL_DATABASE_DB = 'test'
MYSQL_DATABASE_HOST = 'localhost'
SQLALCHEMY_DATABASE_URI = 'mysql://{0}:{1}@{2}/{3}'.format(MYSQL_DATABASE_USER,
                                                           MYSQL_DATABASE_PASSWORD,
                                                           MYSQL_DATABASE_HOST,
                                                           MYSQL_DATABASE_DB)

global db
db = create_engine(SQLALCHEMY_DATABASE_URI)


class MysqlDatabase(object):
    """
    API to access MySQL database.
    """

    def showUsers(self, name):
        rows = db.execute("select * from user where name = \"{0}\";".format(name))
        users = ""
        for row in rows:
            users = users + row[0]
        return users

