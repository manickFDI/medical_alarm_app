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
DISEASE_TABLENAME = "enfermedad"
DISEASE_TABLE_COLUMNS = "idEnfermedad, nombre, erradicada, numMuertes, numNinyos, numAdultos, numAncianos, numMujeres," \
                        "numHombres, peso"
FOCUS_TABLENAME = "foco"
FOCUS_TABLE_COLUMNS = "idFoco, descripcion, numPersonas, idMedico"
FOCUS_PLACES_TABLENAME = "lugaresFoco"
FOCUS_PLACES_TABLE_COLUMNS = "idFoco, lugar, fecha"
CONTAGIONS_TABLENAME = "contagio"
CONTAGIONS_USERS_TABLENAME = "usuarioContagiado"

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

    """
    USERS RELATED FUNCTIONS
    """

    def get_total_user_status(self):

        query = "SELECT count(*) as amount FROM usuario WHERE estado = "

        undef = -1
        healthy = -1
        cured = -1
        infected = -1
        dead = -1
        rows = db.execute(query+"0")  # indef
        if rows is not None and rows.rowcount == 1:
            for row in rows:
                undef = row['amount']

        rows = db.execute(query+"1")  # healthy
        if rows is not None and rows.rowcount == 1:
            for row in rows:
                healthy = row['amount']

        rows = db.execute(query+"2")  # cured
        if rows is not None and rows.rowcount == 1:
            for row in rows:
                cured = row['amount']

        rows = db.execute(query+"3")  # infected
        if rows is not None and rows.rowcount == 1:
            for row in rows:
                infected = row['amount']

        rows = db.execute(query+"4")  # dead
        if rows is not None and rows.rowcount == 1:
            for row in rows:
                dead = row['amount']

        return {'undefined': undef, 'healthy': healthy, 'cured': cured,
                'infected': infected, 'dead': dead}

    """
    USER RELATED FUNCTIONS
    """

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

    def get_user(self, dni):

        query = "SELECT * FROM {0} WHERE dni = \"{1}\"".format(USERS_TABLENAME, dni)
        rows = db.execute(query)

        if rows is None or rows.rowcount > 1:
            return None
        for row in rows:
            return self.create_user_object(row)

    @staticmethod
    def create_user_object(row):

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
                'weight': weight, 'idnumber': idNumber, 'state': state, 'salt': salt}

        return user

    """
    DISEASE RELATED FUNCTIONS
    """

    def get_disease(self, name):

        query = "SELECT *, (numHombres+numMujeres) as numContagions FROM {0} " \
                "WHERE nombre = \"{1}\"".format(DISEASE_TABLENAME, name)
        rows = db.execute(query)

        if rows is None or rows.rowcount > 1:
            return None
        for row in rows:
            return self.create_disease_object(row)

    def get_top_deaths(self, top):

        query = "SELECT nombre, numMuertes FROM {0} ORDER BY numMuertes DESC LIMIT {1}".format(DISEASE_TABLENAME, top)
        rows = db.execute(query)

        ranking = []
        if rows is None or rows.rowcount == 0:
            return None
        for row in rows:
            disease = {'name': row['nombre'], 'num_deaths': str(row['numMuertes'])}
            ranking.append(disease)

        return ranking

    def get_top_contagions(self, top):

        query = "SELECT nombre, (numHombres + numMujeres) as numContagions FROM {0} " \
                "ORDER BY numContagions DESC LIMIT {1}".format(DISEASE_TABLENAME, top)
        rows = db.execute(query)

        ranking = []
        if rows is None or rows.rowcount == 0:
            return None
        for row in rows:
            disease = {'name': row['nombre'], 'num_contagions': str(row['numContagions'])}
            ranking.append(disease)

        return ranking

    @staticmethod
    def create_disease_object(row):

        id = str(row['idEnfermedad'])
        name = row['nombre']
        if row['erradicada'] == 1:
            eradicated = "yes"
        else:
            eradicated = "no"
        numDeaths = str(row['numMuertes'])
        numContagions = str(row['numContagions'])
        numChildren = str(row['numNinyos'])
        numAdults = str(row['numAdultos'])
        numElders = str(row['numAncianos'])
        numWomen = str(row['numMujeres'])
        numMen = str(row['numHombres'])
        weight = str(row['peso'])

        disease = {'disease_id': id, 'name': name, 'eradicated': eradicated,
                   'num_deaths': numDeaths, 'num_contagions': numContagions, 'num_children': numChildren,
                   'num_adults': numAdults, 'num_elders': numElders, 'num_women': numWomen, 'num_men': numMen,
                   'weight': weight}

        return disease

    @staticmethod
    def get_disease_from_id_and_list(disease_list, id):
        for disease in disease_list:
            if disease['disease_id'] == id:
                return disease
        return {}

    """
    FOCUS RELATED FUNCTIONS
    """
    def get_focuses(self):
        query = "SELECT * FROM {0} ".format(FOCUS_TABLENAME)
        placesQuery = "SELECT * FROM {0} WHERE idFoco = ".format(FOCUS_PLACES_TABLENAME)
        rows = db.execute(query)

        focuses = []
        if rows is None or rows.rowcount < 1:
            return {}
        for row in rows:
            focus = self.create_focus_object(row)
            placesRows = db.execute(placesQuery + focus['focus_id'])
            if rows is None or placesRows.rowcount < 1:
                focus['places'] = []
            else:
                places = []
                for placeRow in placesRows:
                    places.append(self.create_place_object(placeRow))
                focus['places'] = places
            focuses.append(focus)
        return focuses

    def delete_focus(self, id):

        existsContagionQuery = "SELECT * FROM {0} WHERE idFoco = {1}".format(FOCUS_TABLENAME, id)
        deleteContagionQuery = "DELETE FROM {0} WHERE idFoco = {1}".format(FOCUS_TABLENAME,id)
        rows = db.execute(existsContagionQuery)

        if rows is not None and rows.rowcount > 0:
            db.execute(deleteContagionQuery)
            return True
        return False

    @staticmethod
    def create_focus_object(row):
        """
            +-------------+-------------+------+-----+---------+-------+
            | Field       | Type        | Null | Key | Default | Extra |
            +-------------+-------------+------+-----+---------+-------+
            | idFoco      | int(11)     | NO   | PRI | NULL    |       |
            | descripcion | varchar(45) | NO   |     | NULL    |       |
            | numPersonas | int(11)     | NO   |     | NULL    |       |
            | idMedico    | int(11)     | NO   | MUL | NULL    |       |
            +-------------+-------------+------+-----+---------+-------+
        """
        id = str(row['idFoco'])
        description = row['descripcion']
        nPeople = str(row['numPersonas'])
        idDoctor = str(row['idMedico'])

        focus = {'focus_id': id, 'description': description, 'num_people': nPeople, 'doctor_id': idDoctor}

        return focus

    @staticmethod
    def create_place_object(placeRow):
        """
            +--------+-------------+------+-----+---------+-------+
            | Field  | Type        | Null | Key | Default | Extra |
            +--------+-------------+------+-----+---------+-------+
            | idFoco | int(11)     | NO   | PRI | NULL    |       |
            | lugar  | varchar(45) | NO   | PRI | NULL    |       |
            | fecha  | varchar(10) | NO   | PRI | NULL    |       |
            +--------+-------------+------+-----+---------+-------+
        """
        id = str(placeRow['idFoco'])
        place = placeRow['lugar']
        date = placeRow['fecha']

        focus = {'focus_id': id, 'place': place, 'date': date}

        return focus


    """
    CONTAGIONS RELATED FUNCTIONS
    """

    def get_users_contagions(self, disease):
        diseaseQuery = "SELECT idEnfermedad FROM {0} WHERE nombre = \"{1}\"".format(DISEASE_TABLENAME, disease);
        rows = db.execute(diseaseQuery)

        diseaseId = -1
        if rows is None or rows.rowcount > 1:
            return {}
        for row in rows:
            diseaseId = row['idEnfermedad']

        contagionsQuery = "SELECT * FROM {0} WHERE idEnfermedad = {1}".format(CONTAGIONS_TABLENAME, diseaseId)
        rows = db.execute(contagionsQuery)

        if rows is None or rows.rowcount < 1:
            return {}
        contagions = []
        for row in rows:
            contagion = self.create_contagion_object(row)

            infectedUsersQuery = "SELECT * FROM {0} WHERE idContagio = {1}".format(CONTAGIONS_USERS_TABLENAME,
                                                                                   contagion['contagion_id'])
            infectedUsersRows = db.execute(infectedUsersQuery)
            if infectedUsersRows is None or infectedUsersRows.rowcount < 1:
                contagion['users']=[]
            else:
                users = []
                for infectedUserRow in infectedUsersRows:
                    userQuery = "SELECT * FROM {0} WHERE idUsuario = {1}".format(USERS_TABLENAME, infectedUserRow['idUsuario'])
                    userRows = db.execute(userQuery)

                    if userRows is not None and userRows.rowcount == 1:
                        for userRow in userRows:
                            users.append(self.create_user_object(userRow))
                contagion['users'] = users

            contagions.append(contagion)

        return contagions

    def get_active_contagions(self):

        # Get diseases
        diseasesQuery = "SELECT *, (numHombres+numMujeres) as numContagions  FROM {0}".format(DISEASE_TABLENAME)
        diseaseRows = db.execute(diseasesQuery)
        if diseaseRows is None or diseaseRows.rowcount < 1:
            return {}
        diseasesData = []
        for diseaseRow in diseaseRows:
            diseasesData.append(self.create_disease_object(diseaseRow))

        contagions = []
        #Get contagions
        contagionsQuery = "SELECT * FROM {0} WHERE nivel > 0".format(CONTAGIONS_TABLENAME)
        contagionRows = db.execute(contagionsQuery)
        if contagionRows is None or contagionRows.rowcount < 1:
            return {}
        for contagionRow in contagionRows:
            contagion = self.create_contagion_object(contagionRow)
            contagion['disease'] = self.get_disease_from_id_and_list(diseasesData, contagion['disease_id'])
            """
            userContagionsQuery = "SELECT count(*) as amount FROM {0} WHERE idContagio = ".format(CONTAGIONS_USERS_TABLENAME, contagion['contagion_id'])
            userContagionRows = db.execute(userContagionsQuery)

            if userContagionRows is None or userContagionRows.rowcount > 1:
                contagion['num_users'] = "0"
            else:
                for userContagionRow in userContagionRows:
                    contagion['num_users'] = userContagionRow['amount']
            """
            contagions.append(contagion)

        return contagions

    def delete_contagion(self, id):

        existsContagionQuery = "SELECT * FROM {0} WHERE idContagio = {1}".format(CONTAGIONS_TABLENAME, id)
        deleteContagionQuery = "UPDATE {0} SET nivel={1} WHERE idContagio = {2}".format(CONTAGIONS_TABLENAME,0, id)
        rows = db.execute(existsContagionQuery)

        if rows is not None and rows.rowcount > 0:
            db.execute(deleteContagionQuery)
            return True
        return False

    @staticmethod
    def create_contagion_object(row):
        """
            +--------------+--------------+------+-----+---------+-------+
            | idContagio   | int(11)      | NO   | PRI | NULL    |       |
            | idEnfermedad | int(11)      | NO   | MUL | NULL    |       |
            | idMedico     | int(11)      | NO   | MUL | NULL    |       |
            | tiempo       | int(11)      | NO   |     | NULL    |       |
            | distancia    | int(11)      | NO   |     | NULL    |       |
            | fecha        | varchar(10)  | NO   |     | NULL    |       |
            | nivel        | int(11)      | NO   |     | NULL    |       |
            | descripcion  | varchar(140) | NO   |     | NULL    |       |
            | zona         | varchar(45)  | NO   |     | NULL    |       |
            +--------------+--------------+------+-----+---------+-------+
        """
        id = str(row['idContagio'])
        idDisease = str(row['idEnfermedad'])
        idDoctor = str(row['idMedico'])
        time = str(row['tiempo'])
        distance = str(row['distancia'])
        level = str(row['nivel'])
        place = row['zona']
        date = row['fecha']
        description = row['descripcion']

        contagion = {'contagion_id': id, 'disease_id': idDisease, 'doctor_id': idDoctor, 'time': time,
                    'distance':distance, 'level': level, 'place': place, 'date': date, 'description': description}

        return contagion