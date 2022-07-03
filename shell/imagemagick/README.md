# Convert code snippets

1. Resize and zoom to explicit Aspect:
```
convert DSC02467.JPG -gravity center -crop 4:3 -resize 800x600^ 800x_600_DSC02467.JPG
```

2. Resize but keep Aspect and add Transparency (add +repage for PNG)
```
convert DSC02467.JPG -resize 800x600^ -size 800x600 xc:none +swap -gravity center -composite 800x_600_DSC02467.webp
```