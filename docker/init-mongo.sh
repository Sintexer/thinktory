set -e

mongosh <<EOF
use $MONGODB_DATABASE

db.createUser({
  user: '$MONGODB_USERNAME',
  pwd: '$MONGODB_PASSWORD',
  roles: [{
    role: 'readWrite',
    db: '$MONGODB_DATABASE'
  }]
});
db.createCollection('topics');
db.createCollection('concepts');
db.createCollection('books');
EOF