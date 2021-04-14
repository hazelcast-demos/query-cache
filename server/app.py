import uuid

from flask import Flask, request, Response, render_template, session
from hazelcast import HazelcastClient
from hazelcast.core import HazelcastJsonValue

app = Flask(__name__)
app.secret_key = 'really really secret key for a demo'
client = HazelcastClient()
analytics = client.get_map("analytics")


@app.route('/')
def home():
    return render_template('index.html')


@app.route('/analytics', methods=['POST'])
def click():
    if 'id' not in session:
        session['id'] = uuid.uuid4()
    session_id = session['id']
    data = request.get_json()
    app.logger.info('Event received: %s', data)
    data['session'] = session_id.hex
    app.logger.info('Event enriched: %s', data)
    analytics.set(uuid.uuid4(), HazelcastJsonValue(data))
    return Response(status=202)


if __name__ == '__main__':
    app.run()
