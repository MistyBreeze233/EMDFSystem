<template>
  <div class="upload">


    <el-upload
      class="upload-demo"
      drag
      action="#"
      multiple
      :before-upload="BeforeUpload"
      >
      <i class="el-icon-upload"></i>
      <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
      <div class="el-upload__tip" slot="tip">只能上传xlsx文件</div>
    </el-upload>
  </div>


</template>

<script>
import axios from 'axios'

export default {
  data(){
    return{


    }

  },
  methods:{
    HandleUpload(file){


    },
    BeforeUpload(file){
      const isLt50M = file.size / 1024 / 1024 < 2
      if (!isLt50M) {
        this.$message.error('上传文档大小不能超过 50MB!')
        return false
      }
      let _type = file.type

      window.console.log(_type)
      if (
        _type.indexOf('vnd.openxmlformats') == -1
      ) {
        this.$message.error('上文档格式不正确，请重新上传!')
        return false
      }

      var formData = new FormData();
      formData.append("file", file);
      formData.append("type",1)
      axios.post('upload', formData).then(res => {
        window.console.log(res)
        if(res.status=="SUCCESS")
        {
          this.$message({
            message: '上传成功',
            type: 'success'
          });
        }
        else
        {
          this.$message.error(res.msg)

        }
      })

    }
  }
}

</script>

<style>

.upload-demo{
  margin: 0 auto; text-align: center

}

</style>
