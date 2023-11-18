set -e

mongosh <<EOF
use $MONGODB_DATABASE

db.createUser({
  user: '$MONGO_DB_USERNAME',
  pwd: '$MONGO_DB_PASSWORD',
  roles: [{
    role: 'readWrite',
    db: '$MONGODB_DATABASE'
  }]
});
db.createCollection('topics');
db.createCollection('concepts');
db.createCollection('books');
EOF