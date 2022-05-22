gh-md-toc ang*.md > angular_toc.md
gh-md-toc spring*.md > spring_toc.md
find -E . -depth 1 -type f ! -iregex "\.\/((ang)|(spring)|(readme)|(pdfslide)).*" -exec echo {} > ~.tmp1 \;
cat ~.tmp1 | grep .md | tr '\n' ' ' > ~.tmp2
files=`cat ~.tmp2`
gh-md-toc  $files > other_toc.md