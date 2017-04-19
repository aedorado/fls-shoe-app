import os
from API import API
from DB import DB
# We'll render HTML templates and access data sent by POST
# using the request object from flask. Redirect and url_for
# will be used to redirect the user once the upload is done
# and send_from_directory will help us to send/show on the
# browser the file that the user just uploaded
from flask import Flask, render_template, request, redirect, url_for, send_from_directory, jsonify
from werkzeug import secure_filename

app = Flask(__name__)	# Initialize the Flask application

app.config['UPLOAD_FOLDER'] = 'uploads/'	# This is the path to the upload directory
app.config['TEST_FOLDER'] = 'test/'    # This is the path to the upload directory
app.config['ALLOWED_EXTENSIONS'] = set(['png', 'jpg', 'jpeg', 'gif'])	# These are the extension that we are accepting to be uploaded

# For a given file, return whether it's an allowed type or not
def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1] in app.config['ALLOWED_EXTENSIONS']

# This route will show a form to perform an AJAX request
# jQuery is loaded to execute the request and update the
# value of the operation

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/test/<filename>')
def tst_images(filename):
    return send_from_directory('test/', filename)

@app.route('/test', methods=['POST'])
def test():
    # Get the name of the uploaded file
    file = request.files['file']
    # Check if the file is one of the allowed types/extensions
    if file and allowed_file(file.filename):
        # Make the filename safe, remove unsupported chars
        filename = secure_filename(file.filename)
        # Move the file form the temporal folder to
        # the upload folder we setup
        image_url = os.path.join(app.config['TEST_FOLDER'], filename)
        file.save(image_url)    # save the file
        image_url = 'https://fls-shoe-app.herokuapp.com/' + image_url
        # print image_url
        json_result =  API().get_json(image_url)
        # print json_result
        # DB().add_data_table(image_url, json_result)
        # return jsonify(json_result)
        return json_result

@app.route('/viewdb')
def viewdb():
    return DB().print_all_data()

@app.route('/up')
def up():
    return render_template('upload.html')

# Route that will process the file upload
@app.route('/upload', methods=['POST'])
def upload():
    # Get the name of the uploaded file
    file = request.files['file']
    # Check if the file is one of the allowed types/extensions
    if file and allowed_file(file.filename):
        # Make the filename safe, remove unsupported chars
        filename = secure_filename(file.filename)
        # Move the file form the temporal folder to
        # the upload folder we setup
        image_url = os.path.join(app.config['UPLOAD_FOLDER'], filename)
        file.save(image_url)	# save the file
        image_url = 'https://fls-shoe-app.herokuapp.com/' + image_url
        # print image_url
        json_result =  API().get_json(image_url)
        # print json_result
        DB().add_data_table(image_url, json_result)
        # return jsonify(json_result)
        return DB().print_all_data()

# This route is expecting a parameter containing the name
# of a file. Then it will locate that file on the upload
# directory and show it on the browser, so if the user uploads
# an image, that image is going to be show after the upload
@app.route('/uploads/<filename>')
def uploaded_file(filename):
    return send_from_directory(app.config['UPLOAD_FOLDER'],
                               filename)

if __name__ == '__main__':
    app.run(
        debug=True
    )