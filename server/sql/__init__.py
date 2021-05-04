import sys
import pprint

from analytics import factory
from hazelcast import HazelcastClient
from hazelcast.predicate import sql

if __name__ == '__main__':
    component = sys.argv[1]
    instant = int(sys.argv[2])
    client = HazelcastClient(
        portable_factories={
            1: factory
        }
    )
    analytics = client.get_map('analytics')
    select = 'component = {component} AND instant < {instant}'.format(component = component, instant = instant)
    results = analytics.values(sql(select)).result()
    pprinter = pprint.PrettyPrinter()
    for result in results:
        pprinter.pprint(result.to_dic())
    client.shutdown()
