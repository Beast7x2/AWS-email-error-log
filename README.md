# AWS-email-error-log
Programs to email "Error" logs from CloudWatch to mail address using Lamda and SNS
To Build

```sh
mvn clean package shade:shade
```

upload this jar to the AWS lambda function.

This function can be invoked by AWS API gateway with sampleValue in the query param

```sh
https://apiurl.com/sampleValue=1
```

if the sampleValue is less than zero then it will log a ERROR message in AWS CloudWatch logs.

To send email for that error. Create another lamda function and add trigger using CloudWatch by selecting the previous lamda function's log group with specific keyword filter
for which you want to send email. In this case I have created filter for "Error" keyword.


Please use below code for second lamda function which publishes message to a SNS topic from where it sends the email

```sh
const AWS = require('aws-sdk');
const zlib = require('zlib');

var sns = new AWS.SNS();

exports.handler = async(event) => {
  const payload = Buffer.from(event.awslogs.data, 'base64');
  const parsed = JSON.parse(zlib.gunzipSync(payload).toString('utf8'));
  console.log('Decoded payload:', JSON.stringify(parsed));

  var params = {
    Message: JSON.stringify(parsed),
    Subject: 'Dev - Applicaton Error',
    TopicArn: 'Error'       //Enter full topic ARN
  };
  var publishTextPromise = new AWS.SNS().publish(params).promise();

  return publishTextPromise.then(
    function(data) {
      console.log(`Message ${params.Message} send sent to the topic ${params.TopicArn}`);
      console.log("MessageID is " + data.MessageId);
    }).catch(
    function(err) {
      console.error(err, err.stack);
    });

};
```

The above function will publish message to "Error" topic in AWS SNS and SNS will send email to all the subscribers of the topic.
