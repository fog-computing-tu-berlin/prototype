from flask import Flask
from flask import request

app = Flask(__name__)


def startFlask(debug=False):
    app.run(host='0.0.0.0', debug=debug, threaded=True)


@app.route("/", methods=['GET'])
def lowerString():
    _strn = request.args.get('param')
    if _strn is not None:
        response = 'lower case of {} is {}'.format(_strn, _strn.lower())
    else:
        response = 'Param not found'
    return response


