#!/usr/bin/env sh
# Wraps src/next-step.html (a page fragment) into a standalone index.html.
set -e
cd "$(dirname "$0")"
{
  printf '<!doctype html>\n<html lang="en">\n<head>\n<meta charset="utf-8">\n'
  printf '<meta name="viewport" content="width=device-width,initial-scale=1,viewport-fit=cover">\n'
  printf '<meta name="theme-color" content="#16505A">\n'
  printf '<style>[hidden]{display:none!important}body{margin:0}:root{padding-top:env(safe-area-inset-top,0px)}</style>\n'
  printf '</head>\n<body>\n'
  cat src/next-step.html
  printf '\n</body>\n</html>\n'
} > index.html
echo "Built index.html"
