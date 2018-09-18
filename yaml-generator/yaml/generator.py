import json
import yaml
import re



written_data = { "swagger" : "2.0", 
                "host": "staging.open-sunbird.org",
                'paths' : {}    
            }

header_params_file = {    "Accept" :               "httpHeaderAccept.txt", 
                        "Content-Type" :               "httpHeaderContentType.txt",
                        "id" :                         "httpHeaderId.txt",
                        "X-Consumer-ID" :              "httpHeaderXConsumerId.txt",
                        "X-Device-ID" :                "httpHeaderXDeviceId.txt",
                        "X-msgid" :                    "httpHeaderXMsgid.txt",   
                        "ts" :                         "httpHeaderTs.txt",
                        "X-Authenticated-Userid" :     "httpHeaderXAuthenticatedUserid.txt",
                        "Authorization" :              "httpHeaderAuthorization.txt",
                        "x-authenticated-user-token" : "httpHeaderXAuthenticatedUserToken.txt" 
                        }

valid_response_code = [200,400,401,404,500]

def json_parser(filepath):
    with open(filepath) as json_file:
        json_file_data = json.load(json_file)
    return json_file_data

def yaml_parser(filepath):
    with open(filepath) as json_file:
        yaml_file_data = yaml.load(json_file)
    return yaml_file_data

def get_apis_detail(data):
    return data['apis']     

def get_header_basepath(data):
    return data['header']['basePath']

def get_json_header(data):
    return data['header']

def get_api_method_error_response_model():
    return {
         400 : {
             "description" : "BAD REQUEST. CHECK THE REQUEST HEADER AND BODY",
             "schema" :{
                 '$ref' : '#/definitions/ErrorResponse'
                        }
         },
         404 : {
             "description" : "Requested resource not found",
             "schema" :{
                 '$ref' : '#/definitions/ErrorResponse'
                        }
         },
         500 : {
             "description" : "You got us. Meanwhile, please keep refreshing and if the issue persists feel free to contact at info@sunbird.org"
         },
         401 : {
             "description" : "Authorization Issue. Give the correct token required",
             "schema" :{
                 '$ref' : '#/definitions/ErrorResponse'
                        }
         }
    
    }


def check_response_code_validity(response_code): 
    return response_code in valid_response_code

def get_parameter_model():
    return {'name' : '',
        'in': 'header',
        'required': True,
        'type': 'string',
        'description' : ''
        }
    
def get_yaml_definitions_response_header():
    return   {  "title" : "ResponseHeader",
                "type" : "object",
                "properties" : {
                    "id": {
                        "type": "string",
                        "description": "API Identifier"
                            },
                    "ver":{
                         "type": "string",
                         "description": "API version information"
                            },
                    "ts":{
                        "type": "string",
                         "description": "API execution timespan"
                        },
                    "params":{
                        "$ref": '#/definitions/ResponseParams'
                        },
                    "responseCode":{
                        "type": "object",
                        "description": 'API response code '
                        }
                            }}
    
def get_yaml_definitions_response_params():
    return {    "title" : "Response Parameter",
                "type" : "object",
                "properties" : {
                    "resmsgid": {
                        "type": "string",
                        "description": "Response Message Id"
                            },
                    "msgid":{
                         "type": "string",
                         "description": "Message Id"
                            },
                    "err":{
                        "type": "string",
                         "description": "Error code"
                        },
                    "status":{
                        "type": "string",
                        "description": "Status code"
                        },
                    "errormsg":{
                        "type": "object",
                        "description": 'Error message'
                        }
                            }}
      
def get_yaml_definitions_error_response():
    return {'title': 'Error Response',
            'type': 'object',
            'allOf':[
                {
                '$ref': '#/definitions/EmptyResult'
                },
                {
                '$ref': '#/definitions/ResponseHeader'
                }
                ]
            }
    
        
def get_yaml_definitions_success_response():
    return {'title': 'Success Response',
            'type': 'object',
            'allOf':[
                {
                '$ref': '#/definitions/ResultWithId'
                },
                {
                '$ref': '#/definitions/ResponseHeader'
                }
                ]
            }
   
def get_yaml_definitions_result_with_id():
    return {'title': 'Result With Some Id',
            'type': 'object',
            'properties' : {
                'result' : {
                    '$ref' : '#/definitions/RequiredId'}
                    }
            }
def get_yaml_definitions_empty_result():
    return {
      'title': 'Empty Result',
      'type': 'object',
      'properties': {
        'result': {
          'type': 'object',
          'description': 'Empty Result'
        }
      }
    }
def get_yaml_definitions_required_id():
    return {    "title" : "Required Id",
                "type" : "object",
                "properties" : {
                    'some_id' : {
                    "type": "string",
                    "description": "Required Result id"
                                }
                            }
            }                          
def check_placeholder(key):
    return re.search(r'[.\n]*(\$\{)([a-zA-Z])*(\})', key) != None

def replace_placeholder(string_value, replacable_value):
    starting_index = re.search(r'[.\n]*(\$\{)([a-zA-Z])*(\})', string_value).start()
    ending_index = re.search(r'[.\n]*(\$\{)([a-zA-Z])*(\})', string_value).end()
    return string_value[:starting_index] + replacable_value + string_value[ending_index+1:]
        
def read_file_lines(filepath) :
    try:
        with open(filepath, 'r') as file_d :
            return file_d.readlines()
    except:
        print('Error in reading file', filepath)
    finally:
        file_d.close()
        
def read_file_line(filepath) :
    try:
        with open(filepath, 'r') as file_d :
            return file_d.readline()
    except:
        print('Error in reading file', filepath)
    finally:
        file_d.close()
        
def iterate_dict(key, value):
    global written_data
    
    for new_key, new_value in value.items() :
        written_data[key][new_key] = new_value
             
def iterate_list(key,value):
    global written_data
    
    for new_value in value:
        written_data[key].append(new_value)
    
def construct_yaml_header(json_header_data):
    global written_data
    
    for key,value in json_header_data.items() :
        if isinstance(value, dict):
            written_data[key] =  {}
            iterate_dict(key,value)
            
        elif isinstance(value, list):
            written_data[key] = []
            iterate_list(key,value)
            
        else:
            written_data[key] = value
            
def yaml_dump(filepath, data):
    with open(filepath, "w") as file_descriptor:
        yaml.dump(data, file_descriptor, default_flow_style = False)        
       
 
def construct_yaml_api_parameter_response(headers, apis, header_basepath):
    global written_data
    headers_list = []
    for keys,value in headers.items():
        if value['required'] :
            header_item = get_parameter_model()
            header_item['name'] = keys
            header_item['required'] = value ['required']
            filepath = header_basepath + header_params_file[keys]
            description = read_file_line(filepath)
        
            if check_placeholder(description) :
                if value.get('value') != None :
                    header_item['description'] = replace_placeholder(description, value['value'])
                else:
                    print('There is a placeholder and you must specify value parameter for the ', keys)
            else:
                header_item['description'] = description             
                
            headers_list.append(header_item)
    k =written_data['paths'] [apis['path']] [apis['method'].lower()]
    k['parameters'] = headers_list
    
    k['responses'] = get_api_method_error_response_model()
    k['responses'] ['200'] = apis['response']['200']
    
    #k['responses'] = get_api_method_response_model()
    
def construct_yaml_definitions():
    global written_data
    written_data['definitions'] = {}
    written_data['definitions']['ResponseHeader'] = get_yaml_definitions_response_header()
    written_data['definitions']['ResponseParams'] = get_yaml_definitions_response_params()
    written_data['definitions']['ErrorResponse']  = get_yaml_definitions_error_response()
    written_data['definitions']['SuccessResponse']= get_yaml_definitions_success_response()
    written_data['definitions']['ResultWithId']   = get_yaml_definitions_result_with_id()
    written_data['definitions']['EmptyResult']    = get_yaml_definitions_empty_result()
    written_data['definitions']['RequiredId']     = get_yaml_definitions_required_id()
    
def get_summary(method):
    summary = {'get' : 'get existing ',
               'post' : 'Create a new resource',
               'put': 'Update an existing',
               'delete':'Delete an existing'}
    return summary[method]
        
def initialize_api_parameter(path, method):
    global written_data
    
    written_data['paths'] [path] = {}
    
    written_data['paths'] [path] [method] = {}
    k = written_data['paths'] [path] [method]
    k ['parameters'] = []
    k ['responses'] = {}
    k ['description'] = 'Write the description of this api method here'
    k ['tags'] = ['Tags of the API']
    k ['summary'] = get_summary(method)
    k['produces'] = ['application/json']
    #written_data['paths'] [path] [method] ['parameters'] : []
    # written_data['paths'] [path] [method] ['responses'] : {}
 
def get_default_definition_template(definition_name):
    return {
        
        }   
if __name__ == '__main__' :
    json_file_path = 'config.json'
    data = json_parser(json_file_path)
    
    '''
    Converting json to yaml header.
    step 1. Get the json header
    step 2. Construct the yaml header
    '''
    json_header_data = get_json_header(data)
    construct_yaml_header(json_header_data)
    
    apis_detail_list = get_apis_detail(data) 
    header_basepath = get_header_basepath(data)
    
    for apis in apis_detail_list :
        initialize_api_parameter(apis['path'],apis['method'].lower())
        construct_yaml_api_parameter_response(apis['headers'],apis,header_basepath)
        
    construct_yaml_definitions()
    '''
    Writing to the config.yaml file
    '''
    written_data = json.loads(json.dumps(written_data))
    yaml_dump('config.yaml',written_data)
    