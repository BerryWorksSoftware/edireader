export DEST=$1

if [ -z "$DEST" ]
then
  echo "1st argument must specify a destination"
  exit 1
else
  echo "Copying project from $PWD to $DEST"
fi

if [ ! -d "$DEST" ]
then
  echo "Directory $DEST must already exist"
  exit 1
fi

cp pom.xml $DEST
cp -R edireader $DEST
cp -R testresults $DEST

echo "Done"