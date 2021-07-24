from flask import Flask, jsonify, request
import numpy as np
import matplotlib.pyplot as plt
from tensorflow.keras.models import Model
import sys, getopt
from tensorflow.keras.preprocessing.image import array_to_img
import tensorflow as tf
print(tf.__version__)

from logging.config import dictConfig

dictConfig({
    'version': 1,
    'formatters': {'default': {
        'format': '[%(asctime)s] %(levelname)s in %(module)s: %(message)s',
    }},
    'handlers': {'wsgi': {
        'class': 'logging.StreamHandler',
        'stream': 'ext://flask.logging.wsgi_errors_stream',
        'formatter': 'default'
    }},
    'root': {
        'level': 'INFO',
        'handlers': ['wsgi']
    }
})

app = Flask(__name__)
model= None
pix_dimX = 128
pix_dimY = 256
pix_color = 1
def load_data_numpy(input_file_path,file_ext = ".png",pix_color = pix_color):
    png_input = plt.imread(input_file_path)

    if(pix_color==1):
      png_input= np.average(png_input, axis=2, weights=[1,1,1])

    if(file_ext ==".jpg"):
      png_input = png_input/255.

    return png_input

def generateImage(inputfile,outputfile):
    input_images= load_data_numpy(inputfile,".png")
    predictedOutput = model.predict(input_images.reshape(-1,pix_dimX,pix_dimY,1))
    predictedOutput = predictedOutput.reshape(pix_dimX,pix_dimY,1)

    im = array_to_img(predictedOutput)
    im.save(outputfile)

@app.route('/model/getpressure', methods = ['GET', 'POST'])#methods = ['GET', 'POST', 'DELETE']
def getPressure():
    if(model == None):
        return jsonify(success=False)

    message_info = 'link success'
    app.logger.info(message_info)

   # inputfile=r"D:\Projects\VanGogh\prototype\devData\1_car\Design\Design_5\BinaryRepresentation.png"
    #outputfile=r"D:\Projects\VanGogh\prototype\devData\1_car\Design\Design_5\test.png"
    outputfile=""

    if request.method == 'POST':
        json_data = request.get_json(force=True)
        inputfile = json_data["file_path"]
        if(inputfile == None):
            return jsonify(success=False)
        outputfile = inputfile+"/"+"pressure.png"
        inputfile += "/"+json_data["filename"]
        app.logger.info(inputfile)
        generateImage(inputfile,outputfile)
    elif request.method == 'GET':
        return jsonify(success=False) # in progress.

    return jsonify(success=True, outputPath =outputfile )

if __name__ == '__main__':
    model = tf.keras.models.load_model("D:\Projects\VanGogh\prototype\AI_model\Vangogh_generator.h5",compile=False)
    app.run(debug=True)