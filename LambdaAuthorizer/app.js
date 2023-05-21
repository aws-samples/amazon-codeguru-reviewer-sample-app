"use strict";

const AWS = require('aws-sdk'); 
let apiKey;

exports.handler = async (event, context, callback) => {        
 
    //get headertoken from browser
    let headerToken = event.headers["AuthorizationToken"];

    //get secretmanager value:
    if(!apiKey)
    {    
        const smClient = new AWS.SecretsManager();
        const secretName = "AuthorizationToken";
        const secret = await smClient.getSecretValue({SecretId: secretName}).promise();
        apiKey = JSON.parse(secret.SecretString).apiKey;
    }

    // Parse the input for the parameter values
    var tmp = event.methodArn.split(':');
    var apiGatewayArnTmp = tmp[5].split('/');

    // Create wildcard resource
    var resource = tmp[0] + ":" + tmp[1] + ":" + tmp[2] + ":" + tmp[3] + ":" + tmp[4] + ":" + apiGatewayArnTmp[0] + '/*/*'; 
    console.log("resource: " + resource);
        
    // Perform authorization to return the Allow policy for correct parameters and 
    // the 'Unauthorized' error, otherwise.
    var authResponse = {};
    var condition = {};
    condition.IpAddress = {};
    if (headerToken === apiKey) {
        callback(null, generateAllow('me', resource));
    }  else {
        callback("Unauthorized");
    }
};
    
// Help function to generate an IAM policy
var generatePolicy = function(principalId, effect, resource) {
    // Required output:
    console.log("Resource in generatePolicy(): " + resource);
    var authResponse = {};
    authResponse.principalId = principalId;
    if (effect && resource) {
        var policyDocument = {};
        policyDocument.Version = '2012-10-17'; // default version
        policyDocument.Statement = [];
        var statementOne = {};
        statementOne.Action = 'execute-api:Invoke'; // default action
        statementOne.Effect = effect;
        statementOne.Resource = resource;
        console.log("***Resource*** " + resource);
        policyDocument.Statement[0] = statementOne;
        console.log("***Generated Policy*** ");
        console.log(policyDocument);
        authResponse.policyDocument = policyDocument;
    }
   
    return authResponse;
}
    
var generateAllow = function(principalId, resource) {
    return generatePolicy(principalId, 'Allow', resource);
}
    
var generateDeny = function(principalId, resource) {
    return generatePolicy(principalId, 'Deny', resource);
}