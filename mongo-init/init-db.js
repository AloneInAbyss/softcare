// MongoDB initialization script for SoftCare
// This script creates the database and basic collections

db = db.getSiblingDB('softcare');

// Create collections with validation schemas
db.createCollection('users', {
   validator: {
      $jsonSchema: {
         bsonType: "object",
         required: ["email", "createdAt"],
         properties: {
            email: {
               bsonType: "string",
               description: "must be a string and is required"
            },
            createdAt: {
               bsonType: "date",
               description: "must be a date and is required"
            }
         }
      }
   }
});

db.createCollection('psychosocial_assessments');
db.createCollection('emotional_diaries');
db.createCollection('support_channels');
db.createCollection('audit_logs');

// Create indexes for better performance
db.users.createIndex({ "email": 1 }, { unique: true });
db.users.createIndex({ "createdAt": 1 });
db.psychosocial_assessments.createIndex({ "userId": 1, "createdAt": -1 });
db.emotional_diaries.createIndex({ "userId": 1, "date": -1 });
db.audit_logs.createIndex({ "timestamp": -1 });
db.audit_logs.createIndex({ "userId": 1, "timestamp": -1 });

print("SoftCare database initialized successfully!");