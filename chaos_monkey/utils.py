import io
import configparser
import os
import configparser


def parse_properties(filename):
    with open(filename) as f:
        config = io.StringIO()
        config.write('[default]\n')
        config.write(open(filename).read())
        config.seek(0, os.SEEK_SET)
        properties = configparser.ConfigParser()
        properties.read_file(config)
        return dict(properties.items("default"))