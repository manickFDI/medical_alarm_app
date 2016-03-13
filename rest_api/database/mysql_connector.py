from sqlalchemy import create_engine

MYSQL_DATABASE_USER = 'root'
MYSQL_DATABASE_PASSWORD = 'rooting'
MYSQL_DATABASE_DB = 'malarm'
MYSQL_DATABASE_HOST = 'localhost'
SQLALCHEMY_DATABASE_URI = 'mysql://{0}:{1}@{2}/{3}'.format(MYSQL_DATABASE_USER,
                                                           MYSQL_DATABASE_PASSWORD,
                                                           MYSQL_DATABASE_HOST,
                                                           MYSQL_DATABASE_DB)

"""
DATABASE MACROS
"""
USERS_TABLENAME = "usuario"
USERS_TABLE_COLUMNS = "nombre, apellidos, email, fechaNacimiento, sexo, peso, DNI, secret, salt, estado"


global db
db = create_engine(SQLALCHEMY_DATABASE_URI)


class MysqlDatabase(object):
    """
    API to access MySQL database.
    """

    def init(self, db_path):
        with open(db_path, 'r') as content_file:
            content = content_file.read()
            db.execute(content)
        return True

    def contains_user_email(self, email):
        return False

    def contains_user_id_number(self, idNumber):
        return False

    def create_user(self, name, lastName, email, birthday, gender, weight, idNumber, secret, salt, state):

        userValues = "\"{0}\", \"{1}\", \"{2}\", \"{3}\", {4}, {5}, \"{6}\", \"{7}\", \"{8}\", {9}".format(
                    name, lastName, email, birthday, gender, weight, idNumber, secret, salt, state
        )

        query = "INSERT INTO {0} ({1}) VALUES ({2})".format(USERS_TABLENAME, USERS_TABLE_COLUMNS, userValues)

        db.execute(query)

        return 0

    def get_user(self,email):

        query = "SELECT * FROM {0} WHERE email = \"{1}\"".format(USERS_TABLENAME, email)
        rows = db.execute(query)

        if rows is None or rows.rowcount > 1:
            return None
        for row in rows:
            return self.create_user_object(row)

    @staticmethod
    def create_user_object(row):
        """
        It takes a database Row and transform it into a python dictionary.
        Dictionary contains the following keys:
          - userid: id of the user (int)
          - nickname: user's nickname
        Note that all values in the returned dictionary are string unless
        otherwise stated.
        :param row: row containing user data from mysql query
        """
        id = str(row['idUsuario'])
        name = row['nombre']
        lastname = row['apellidos']
        email = row['email']
        birthday = row['fechaNacimiento']
        gender = str(row['sexo'])
        weight = row['peso']
        idNumber = row['DNI']
        state = row['estado']
        salt = row['salt']

        user = {'user_id': id, 'name': name, 'lastname': lastname,
                'email': email, 'birthday': birthday, 'gender': gender,
                'weight': weight, 'idnumber': idNumber, 'state':state, 'salt':salt}

        return user