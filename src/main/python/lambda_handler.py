import boto3
import subprocess

successes = 0

# Dummy AWS Handler to kick off high level processes
def lambda_handler(source_region, destination_region, credentials):

    session = boto3.Session()

    # Load Records into KINESIS
    CLIENT_NAME = 'kinesis'
    kinesis = session.client(CLIENT_NAME, region_name=source_region, aws_access_key_id=credentials,
                             aws_secret_access_key=credentials['SecretAccessKey'],
                             aws_session_token=credentials['SessionToken'])
    process_kinesis(kinesis, "some_file_path.txt")

    # Get SNS Topic ARNs
    CLIENT_NAME = 'sns'
    for region in [source_region, destination_region]:
        sns = session.client(CLIENT_NAME, region_name=region, aws_access_key_id=credentials,
                             aws_secret_access_key=credentials['SecretAccessKey'],
                             aws_session_token=credentials['SessionToken'])
        topic_arns = list_sns(sns)
        print(len(topic_arns))

    # Sync Source DDB to Destination Region
    CLIENT_NAME = 'dynamodb'
    source_ddb = session.client(CLIENT_NAME, region_name=source_region, aws_access_key_id=credentials['AccessKeyId'],
                              aws_secret_access_key=credentials['SecretAccessKey'],
                              aws_session_token=credentials['SessionToken'])

    destination_ddb = session.client(CLIENT_NAME, region_name=destination_region)
    sync_ddb_table(source_ddb, destination_ddb)


# Scan returns paginated results, so only partial data will be copied
def sync_ddb_table(source_ddb, destination_ddb):
    response = source_ddb.scan(
        TableName="table1"
    )
    for item in response['Items']:
        destination_ddb.put_item(
            TableName="table2",
            Item=item
        )

# This code uses a mutable default argument and modifies it to return. This would leak results across calls
def list_sns(sns, topics=[]):
    response = sns.list_topics()
    for topic_arn in response["Topics"]:
        topics.append(topic_arn["TopicArn"])
    return topics


# Infinite loop because a list is modified while being iterated over, Indices are not updated.
def infinite_loop():
    words = ['aws', 'amazon', 'codeguru']
    for w in words:
        if len(w) > 4:
            words.insert(0, w)
    return words

# Prefer DefaultDict over setDefult
def setdefault_example():
    std_dict = dict()
    for k, v  in enumerate(range(5)):
        std_dict.setdefault(k, []).append(v)
    return std_dict

# This method reads multiple file paths, open each file to load data, but forgets to close it, leading to resource leaks
# Further, it selectively processes content based on string find condition. The find() operation can be simply replaced
# with a membership tests because one does not need to know the position at which the search keyword appears.
def process_kinesis(kinesis, file_list_path):
    flp = open(file_list_path, 'r')
    for line in flp:
        file_path = line.strip('\n').strip('\r\n')
        fp = open(file_path, 'r')
        for content in fp:
            if content.find("kinesis") != -1:
                record = load_kinesis_record(content)
                save_kinesis_record(kinesis, record)

# Do not call this function unless you're sure that the "cmd" is secure to run
# This function can be misused to carry out shell injection attacks.
# Further, the code is simply passing an exception, which is not the best practice
# Further, the code keeps track of successful loads by writing to a global variable; which can lead to inaccuracies in
# case of concurrent read/writes to the global variable.

def load_kinesis_record(cmd, mode='subprocess'):
    global successes
    kinesis_record = None
    try:
        if mode == "subprocess":
            kinesis_record = subprocess.call(cmd, shell=True)
        else:
            kinesis_record = eval(cmd)
        successes += 1
    except Exception as e:
        pass
    return kinesis_record


# This code saves records to Kinesis, but does not check and retry for failed records
# Further, it simply re-raises the caught exception without any additional steps. This redundancy will be flagged.
def save_kinesis_record(kinesis_client, record):
    try:
        kinesis_client.put_records(record)
    except:
        raise
