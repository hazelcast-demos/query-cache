from hazelcast.serialization.api import Portable


class Analytics(Portable):
    def __init__(self, dic=None):
        if dic is not None:
            self.timestamp = dic['timestamp']
            self.event = dic['event']
            self.instant = dic['instant']
            self.component = dic['component']
            self.session = dic['session']
            self.client = dic['client']
            self.offset = dic['offset']
            self.scroll = dic['scroll']
            if 'value' in dic.keys():
                self.value = dic['value']
            else:
                self.value = None
            if 'type' in dic.keys():
                self.type = dic['type']
            else:
                self.type = None
            if 'x' in dic.keys():
                self.x = dic['x']
            else:
                self.x = None
            if 'y' in dic.keys():
                self.y = dic['y']
            else:
                self.y = None

    def get_class_id(self):
        return 1

    def get_factory_id(self):
        return 1

    def write_portable(self, writer):
        writer.write_long('timestamp', self.timestamp)
        writer.write_long('instant', self.instant)
        writer.write_utf('event', self.event)
        writer.write_utf('component', self.component)
        writer.write_utf('session', self.session)
        writer.write_int('offset.left', self.offset['left'])
        writer.write_int('offset.height', self.offset['height'])
        writer.write_int('offset.top', self.offset['top'])
        writer.write_int('offset.width', self.offset['width'])
        writer.write_int('client.left', self.client['left'])
        writer.write_int('client.height', self.client['height'])
        writer.write_int('client.top', self.client['top'])
        writer.write_int('client.width', self.client['width'])
        writer.write_int('scroll.left', self.scroll['left'])
        writer.write_int('scroll.height', self.scroll['height'])
        writer.write_int('scroll.top', self.scroll['top'])
        writer.write_int('scroll.width', self.scroll['width'])
        if self.value is not None:
            writer.write_utf('value', str(self.value))
        if self.type is not None:
            writer.write_utf('type', self.type)
        if self.x is None:
            writer.write_int('x', -1)
        else:
            writer.write_int('x', self.x)
        if self.y is None:
            writer.write_int('y', -1)
        else:
            writer.write_int('y', self.y)

    def read_portable(self, reader):
        self.timestamp = reader.read_long('timestamp')
        self.instant = reader.read_long('instant')
        self.event = reader.read_utf('event')
        self.component = reader.read_utf('component')
        self.session = reader.read_utf('session')
        self.offset = {
            'left': reader.read_int('offset.left'),
            'height': reader.read_int('offset.height'),
            'top': reader.read_int('offset.top'),
            'width': reader.read_int('offset.width')
        }
        self.client = {
            'left': reader.read_int('client.left'),
            'height': reader.read_int('client.height'),
            'top': reader.read_int('client.top'),
            'width': reader.read_int('client.width')
        }
        self.scroll = {
            'left': reader.read_int('scroll.left'),
            'height': reader.read_int('scroll.height'),
            'top': reader.read_int('scroll.top'),
            'width': reader.read_int('scroll.width')
        }

    def to_dic(self):
        dic = {
            'timestamp': self.timestamp,
            'event': self.event,
            'instant': self.instant,
            'component': self.component,
            'session': self.session,
            'client': self.client,
            'offset': self.offset,
            'scroll': self.scroll,
        }
        if hasattr(self, 'value'):
            dic['value'] = self.value
        if hasattr(self, 'type'):
            dic['type'] = self.type
        if hasattr(self, 'x'):
            dic['x'] = self.x
        if hasattr(self, 'y'):
            dic['y'] = self.y
        return dic


factory = {
    1: Analytics
}
