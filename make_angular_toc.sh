gh-md-toc ang*.md > angular_toc.md
gh-md-toc spring*.md > spring_toc.md
find -E . -depth 1 -type f ! -iregex "\.\/((ang)|(spring)|(readme)|(pdfslide)).*" -print
