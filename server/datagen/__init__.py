import sys
import uuid
from random import randrange, choice
from time import time_ns

from faker import Faker
from hazelcast import HazelcastClient

from analytics import Analytics, factory

_fake = Faker()
_sessions = {str(uuid.uuid4()) for _ in range(0, randrange(4, 9))}


def _rand_text_field():
    height = 56
    left = 0
    top = 0
    width = randrange(424, 1294)
    dic = {
        'type': 'text',
        'event': 'change',
        'scroll': {
            'height': height,
            'left': left,
            'top': top,
            'width': width
        },
        'client': {
            'height': height,
            'left': left + 1,
            'top': top + 1,
            'width': width
        },
        'offset': {
            'height': height + 2,
            'left': left,
            'top': top,
            'width': width + 2
        }
    }
    return dic


def _rand_first_name():
    text_field = _rand_text_field()
    text_field['value'] = _fake.first_name()
    text_field['component'] = 'first-name'
    return text_field


def _rand_last_name():
    text_field = _rand_text_field()
    text_field['value'] = _fake.last_name()
    text_field['component'] = 'last-name'
    return text_field


def _rand_button():
    height = 36
    left = 0
    top = 0
    width = 85
    dic = {
        'y': randrange(254, 289),
        'type': 'button',
        'event': 'click',
        'scroll': {
            'height': height,
            'left': left,
            'top': top,
            'width': width
        },
        'client': {
            'height': height,
            'left': left + 1,
            'top': top + 1,
            'width': width
        },
        'offset': {
            'height': height + 2,
            'top': top + 252,
            'width': width + 2
        }
    }
    return dic


def _rand_success_button():
    button = _rand_button()
    button['x'] = randrange(139, 644)
    button['value'] = 'Success'
    button['component'] = 'success-button'
    button['offset']['left'] = button['scroll']['left'] + 559
    return button


def _rand_first_button():
    button = _rand_button()
    button['x'] = randrange(1039, 1116)
    button['value'] = 'Primary'
    button['component'] = 'first-button'
    button['offset']['left'] = button['scroll']['left'] + 1035
    return button


def _rand_range():
    height = 24
    left = 0
    top = 0
    width = randrange(356, 1226)
    dic = {
        'value': randrange(0, 100),
        'type': 'range',
        'event': 'change',
        'component': 'range',
        'scroll': {
            'height': height,
            'left': left,
            'top': top,
            'width': width
        },
        'client': {
            'height': height,
            'left': left,
            'top': top,
            'width': width
        },
        'offset': {
            'left': left + randrange(82, 262),
            'height': height,
            'top': top + 204,
            'width': width
        }
    }
    return dic


def _rand_event(instant):
    component = choice([_rand_first_button, _rand_success_button, _rand_range, _rand_first_name, _rand_last_name])()
    component['timestamp'] = time_ns()
    component['session'] = choice(list(_sessions))
    component['instant'] = instant
    return Analytics(component)


if __name__ == '__main__':
    size = int(sys.argv[1])
    events = {uuid.uuid4(): _rand_event(i) for i in range(0, size)}
    client = HazelcastClient(
        portable_factories={
            1: factory
        }
    )
    analytics = client.get_map("analytics")
    future = analytics.put_all(events).result()
