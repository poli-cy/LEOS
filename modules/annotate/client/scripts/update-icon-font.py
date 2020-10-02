#!/usr/bin/env python

import argparse
import os
from base64 import b64encode
from zipfile import ZipFile


def main():
    parser = argparse.ArgumentParser('Update the icomoon icon font from the provided archive')
    parser.add_argument('archive', help='Path to .zip file generated by icomoon')
    args = parser.parse_args()

    script_dir = os.path.dirname(os.path.abspath(__file__))
    vendor_style_dir = script_dir + '/../src/styles/vendor'

    icon_font_archive = ZipFile(args.archive)
    icon_font_archive.extract('selection.json', vendor_style_dir + '/fonts')
    icon_font_archive.extract('fonts/h.woff', vendor_style_dir)
    css_input_file = icon_font_archive.open('style.css')

    css_output_file = open(vendor_style_dir + '/icomoon.css', 'w')

    for line in css_input_file:
        if "format('woff')" in line:
            # Rewrite the H font URL
            css_output_file.write("  src: url('../fonts/h.woff') format('woff');\n")
        elif "url(" in line:
            # skip non-WOFF format fonts
            pass
        else:
            css_output_file.write(line)


if __name__ == '__main__':
    main()
