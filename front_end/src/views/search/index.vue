<template>

  <div id="app">

    <sticky :z-index="10" :class-name="'sub-navbar '">



      <div id="select">

        <el-input placeholder="请输入内容" v-model="search" class="input-with-select">
          <el-select v-model="SearchSelect" slot="prepend" placeholder="请选择" class="search-select">
            <el-option label="原因" value="cause"></el-option>
            <el-option label="解决措施" value="measure"></el-option>
            <el-option label="故障现象" value="phen"></el-option>
          </el-select>
          <el-button slot="append" icon="el-icon-search" @click="SearchNode"></el-button>
        </el-input>



        <el-button type="primary" @click="r">查看全部</el-button>
        <el-button type="primary" @click="t">测试</el-button>

        <el-button type="success" @click="AddDialogVisible = true">添加节点</el-button>

      </div>
    </sticky>

    <el-dialog
      title="搜索结果"
      :visible.sync="SearchDialogVisible"
      width="30%"

      @open="SearchDialogOpen"
    >

    </el-dialog>

    <el-dialog
      title="编辑信息"
      :visible.sync="EditDialogVisible"
      width="30%"

    >
      <el-form ref="form" :model="val" label-width="80px">
        <el-form-item label="id">

          <el-input v-model="val.id"></el-input>


        </el-form-item>

        <el-form-item label="name">

          <el-input type="textarea" autosize v-model="val.entityName"></el-input>


        </el-form-item>
        <el-form-item label="Label">

          <el-input v-model="val.entityLabel"></el-input>


        </el-form-item>

        <el-form-item>
          <el-button @click="EditDialogVisible = false">取 消</el-button>

          <el-button type="primary" @click="submitForm('form')">提交</el-button>
        </el-form-item>
      </el-form>

    </el-dialog>

    <el-dialog
      title="添加节点"
      :visible.sync="AddDialogVisible"
      width="30%"

      @open="addDialogOpen"
    >
      <el-form ref="addform" :model="AddFormData" label-width="80px">
        <el-form-item label="entityid" prop="entityid">

          <el-input v-model="AddFormData.entityid"></el-input>


        </el-form-item>


        <el-form-item label="name" prop="name">

          <el-input v-model="AddFormData.name"></el-input>
        </el-form-item>

        <el-form-item label="device" prop="device">
          <el-select v-model="AddFormData.device" placeholder="请选择设备">
            <el-option
              v-for="item in devicelist"
              :key="item.id"
              :label="item.name"
              :value="item.name">


            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="label" prop="label">
          <el-select v-model="AddFormData.label" placeholder="请选择节点类型">
            <el-option label="现象" value="phen"></el-option>
            <el-option label="原因" value="cause"></el-option>
            <el-option label="解决措施" value="measure"></el-option>
          </el-select>
        </el-form-item>


        <el-form-item>
          <el-button @click="AddDialogVisible = false">取 消</el-button>

          <el-button type="primary" @click="submitadd('form')">提交</el-button>
        </el-form-item>
      </el-form>

    </el-dialog>


    <div id="graph"
         v-loading="loading"

    ></div>
  </div>
</template>

<script>


    import * as d3 from "d3";
    import Sticky from '@/components/Sticky' // 粘性header组件
    import qs from 'qs'
    import axios from 'axios'
    import {
        getNodeSelfPath,
        setLinkGroup,
        getNodesLine,} from './deviceRelation'

export default {
  components: { Sticky},
  data() {
    return {
      search:'',
      SearchSelect:'',
      tip:{},//顶部调试标签 存储当前鼠标停留节点ID
      AddFormData: {
        entityid:'',
        name:'',
        label:'',
        device:''
      },//添加节点表单数据
      loading: false//加载动效
,
      SearchDialogVisible:false,
      AddDialogVisible:false,//添加节点对话框可视
      EditDialogVisible: false,//编辑节点对话框可视
      devicelist:[],//设备列表
      allNodes:{},//所有节点
      finNodes:[],
      allLinks:[],//所有关系
      val:{},
      inputdata:[],

        options: [{
          value: '选项1',
          label: '图谱1'
        }, {
          value: '选项2',
          label: '图谱2'
        }, {
          value: '选项3',
          label: '图谱3'
        }, {
          value: '选项4',
          label: '图谱4'
        }, {
          value: '选项5',
          label: '图谱5'
        }],
      value: ''

    };
  },
    mounted() {
      axios.get('device/find/all').then(res => {

        window.console.log(res)
        this.devicelist=res.data;
      })//初始化时获取所有设备

    },
    methods: {
      SearchNode()
      {
        this.SearchDialogVisible=true;
        axios.get(this.SearchSelect+'/find/all').then(res => {
          //this.allNodes=this.allNodes.concat(res.data);
          window.console.log(res.data);

        })
       // window.console.log(this.search);
      },
      submitForm(formName) {
        this.editInfo();//确认编辑

      },
      submitadd(formName) {
        this.addconfirm();//确认添加

      },
      SearchDialogOpen() {
        try {//防止第一次时输出报错
          this.$refs.addform.resetFields();//清除表单

        }
        catch (e) {
        }

      },
      addDialogOpen() {
        try {//防止第一次时输出报错
          this.$refs.addform.resetFields();//清除表单

        }
        catch (e) {
        }

      },
      async getAllNodesAndLinks()
      {

        this.allNodes=[];//清空
        this.finNodes=[];
        window.console.log(this.allNodes.length);
        window.console.log(this.finNodes.length);
        //window.console.log(this.allNodes);

        //获取所有节点信息
        await axios.get('cause/find/all').then(res => {
          this.allNodes=this.allNodes.concat(res.data);

        })
        await axios.get('defects/find/all').then(res => {
          for(let defect of res.data)
          {

            if(defect.d_p_relations.length!=0)
            {
              defect['entityLabel']='缺陷测试';

              window.console.log(defect)
              this.allNodes.push(defect)
            }
          }
          //this.allNodes=this.allNodes.concat(res.data);

        })
        await axios.get('measure/find/all').then(res => {
          this.allNodes=this.allNodes.concat(res.data);

        })
        await axios.get('phen/find/all').then(res => {
          this.allNodes=this.allNodes.concat(res.data);

        })

        window.console.log(this.allNodes)

        this.allNodes.forEach(item => {
          this.finNodes.push(item)
        })

      },
      async createLinks() {
        this.allLinks=[];
        let i=0;
        for(let item of this.allNodes)
        {

          window.console.log(item.entityLabel);
          if(item.entityLabel=="原因")
          {
            if(item.c_m_relations.length!=0)
            {
              let tar=item.c_m_relations[0].measureEntity.id;
              let finIndex=0;
              let index=0;

              for(let item of this.allNodes)
              {
                if(item.id==tar)
                {
                  finIndex=index;
                }
                index++;
              }
              this.allLinks.push({
                  "source": i,
                  "target": finIndex,
                  "relation": {
                    "relationshipId": 200,
                    "relationship": "原因->解决措施",
                    "created": 1574822650825,
                    "lineNumber": 1,
                    "isSelf": false
                  }
                });
            }
            //以下为旧方法
            //   await axios.get('cause/find/relation/outgoing', {params: ({id: item.id})}).then(res => {
            //       //this.finNodes.push(res.data[0].measureEntity);
            //   window.console.log(res.data[0].measureEntity)
            //   let finIndex=0;
            //   let index=0;
            //   for(let item of this.allNodes)
            //   {
            //     if(item.id==res.data[0].measureEntity.id)
            //     {
            //       finIndex=index;
            //     }
            //     index++;
            //   }
            //   this.allLinks.push({
            //       "source": i,
            //       "target": finIndex,
            //       "relation": {
            //         "relationshipId": 200,
            //         "relationship": "原因->解决措施",
            //         "created": 1574822650825,
            //         "lineNumber": 1,
            //         "isSelf": false
            //       }
            //     });
            //   window.console.log(finIndex);
            // })
          }
          if(item.entityLabel=="现象")
          {
            if(item.p_m_relations.length!=0)
            {
              let tar=item.p_m_relations[0].measureEntity.id;
              let finIndex=0;
              let index=0;

              for(let item of this.allNodes)
              {
                if(item.id==tar)
                {
                  finIndex=index;
                }
                index++;
              }
              this.allLinks.push({
                "source": i,
                "target": finIndex,
                "relation": {
                  "relationshipId": 200,
                  "relationship": "现象->解决措施",
                  "created": 1574822650825,
                  "lineNumber": 1,
                  "isSelf": false
                }
              });
            }

            if(item.p_c_relations.length!=0)
            {
              let dti=0;
              for(let dt of item.p_c_relations)
              {
                let tar=item.p_c_relations[dti].causeEntity.id;
                let finIndex=0;
                let index=0;

                for(let item of this.allNodes)
                {
                  if(item.id==tar)
                  {
                    finIndex=index;
                  }
                  index++;
                }
                this.allLinks.push({
                  "source": i,
                  "target": finIndex,
                  "relation": {
                    "relationshipId": 200,
                    "relationship": "现象->原因",
                    "created": 1574822650825,
                    "lineNumber": 1,
                    "isSelf": false
                  }
                });
                dti++;
              }

            }
            // await axios.get('phen/find/relation/outgoing', {params: ({id: item.id})}).then(res => {
            //   //this.finNodes.push(res.data[0].measureEntity);
            //  // window.console.log(res.data[0].causeEntity)
            //   for(let dt of res.data)
            //   {
            //
            //     let finIndex=0;
            //     let index=0;
            //     for(let item of this.allNodes)
            //     {
            //       if(item.id==dt.causeEntity.id)
            //       {
            //         finIndex=index;
            //       }
            //       index++;
            //     }
            //     this.allLinks.push({
            //       "source": i,
            //       "target": finIndex,
            //       "relation": {
            //         "relationshipId": 200,
            //         "relationship": "现象->原因",
            //         "created": 1574822650825,
            //         "lineNumber": 1,
            //         "isSelf": false
            //       }
            //     });
            //   }
            //
            //   //window.console.log(finIndex);
            // })
          }
          if(item.entityLabel=='缺陷测试')
          {
            if(item.d_p_relations.length!=0)
            {
              let dti=0;
              for(let dt of item.d_p_relations)
              {
                let tar=item.d_p_relations[dti].phenEntity.id;
                let finIndex=0;
                let index=0;

                for(let item of this.allNodes)
                {
                  if(item.id==tar)
                  {
                    finIndex=index;
                  }
                  index++;
                }
                this.allLinks.push({
                  "source": i,
                  "target": finIndex,
                  "relation": {
                    "relationshipId": 200,
                    "relationship": "缺陷测试->现象",
                    "created": 1574822650825,
                    "lineNumber": 1,
                    "isSelf": false
                  }
                });
                dti++;
              }

            }
          }
          i++;
        }


      },
      async r(){//主渲染函数

        this.loading=true;//开启加载动效
        await this.getAllNodesAndLinks()//请求信息
        //window.console.log(this.finNodes)
        await this.createLinks();
        //window.console.log(this.allLinks)
        this.loading=false;
        let data2={"links":this.allLinks,"nodes":this.finNodes};//包含节点与关系
        window.console.log(data2);

        this.renderGraph(data2);
        //window.console.log(data)

      },
      t() {//测试用函数

      },
      editInfo() {//编辑信息

        this.EditDialogVisible=false;
        this.$confirm('此操作将修改节点信息, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          window.console.log(this.val)
          let lab='';//取得所选节点标签信息
          if(this.val.entityLabel=="解决措施")
          {
            lab='measure';
          }
          if(this.val.entityLabel=="原因")
          {
            lab='cause';
          }
          if(this.val.entityLabel=="现象")
          {
            lab='phen';
          }
            axios.post(lab+'/update', qs.stringify({ id:this.val.id,name: this.val.entityName })).then(res => {
              window.console.log(res)
            })
          this.$message({
            type: 'success',
            message: '修改成功!'
          });
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消修改'
          });
        });
      },
      addconfirm() {//添加节点二次确认
        this.AddDialogVisible=false;
        this.$confirm('是否添加节点?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          window.console.log(this.AddFormData)
          axios.post(this.AddFormData.label+'/add', qs.stringify({ name:this.AddFormData.name,entityId:this.AddFormData.entityid,device:this.AddFormData.device })).then(res => {
            window.console.log(res)
          })

          this.$message({

            type: 'success',
            message: '添加成功!'
          });
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消添加'
          });
        });
      },
      deletenode(node,nid) {//删除节点

        this.EditDialogVisible=false;
        this.$confirm('此操作将删除节点, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          window.console.log(node)
          let lab='';
          if(node.entityLabel=="解决措施")
          {
            lab='measure';
          }
          if(node.entityLabel=="原因")
          {
            lab='cause';
          }
          if(node.entityLabel=="现象")
          {
            lab='phen';
          }
          window.console.log(lab)
          axios.post(lab+'/delete', qs.stringify({ id:nid })).then(res => {

          window.console.log(res)

          })

          this.$message({
            type: 'success',
            message: '删除成功!'
          });
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          });
        });
      },
      deletelink() {//删除关系（暂未写）

        this.$confirm('此操作将删除关系, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$message({
            type: 'success',
            message: '删除成功!'
          });
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          });
        });
      },
        renderGraph(data) {
            let {links, nodes} = data;

            //关系分组
            setLinkGroup(links);

            //点击节点后弹出的灰色圆圈以及图标的定义，分为四组【1.删除、2.联系、3.解除位置锁定 4.编辑节点信息】
            let [removePath,remove_rect1,remove_rect2,remove_line1,remove_line2,remove_line3] = [null];
            let [unlockPath,unlock_path1,unlock_rect1,unlock_line1] = [null];
            let [hidePath,hide_path1,hide_path2,hide_path3,hide_path4,hide_circle1,hide_line1] = [null];
            let [editPath,edit_path1,edit_rect1,edit_line1] = [null];
            let rightArc = d3.svg.arc().outerRadius(60).innerRadius(30).startAngle(0).endAngle(1.475);
            let leftArc = d3.svg.arc().outerRadius(60).innerRadius(30).startAngle(1.575).endAngle(3.05);
            let bottomArc = d3.svg.arc().outerRadius(60).innerRadius(30).startAngle(3.15).endAngle(4.525);
            let editArc = d3.svg.arc().outerRadius(60).innerRadius(30).startAngle(4.625).endAngle(6.2);

            d3.select("#graph").html('');

            const force = d3.layout.force()//layout将json格式转化为力学图可用的格式
                .nodes(nodes)//设定节点数组
                .links(links)//设定连线数组
                .linkDistance(150)//连接线长度
                .charge(-1500)//顶点的电荷数。该参数决定是排斥还是吸引，数值越小越互相排斥
                .on("tick", tick)//指时间间隔，隔一段时间刷新一次画面
                .start();//开始转换

            let zoom = d3.behavior.zoom()
                .scaleExtent([.4, 2])
                .on("zoom", zoomed);

            const svg = d3.select("#graph").append("svg")
                .attr("pointer-event", "all")
                .attr("preserveAspectRatio", "xMidYMid meet")//自适应容器大小
                .attr("viewBox", "0 0 1000 1000")
                .call(zoom);

            //设置连接线
            const edges_line = svg.append("g").selectAll(".edgepath")
                .data(force.links())
                .enter()
                .append("path")
                .attr("marker-end",  function (d, i) { return getMarkerArrow(i); })//根据箭头标记的id号标记箭头
                .style("stroke", '#9aaabf')
                .style("stroke-width", 1)//线条粗细
                .style("fill-opacity",0)
                .style("cursor","pointer")
                .attr("id", function (d, i) { return 'edgepath' + i; })
                .on("mouseover", function(d){ return getStrokeWidth(d); })
                .on("mouseout", function() { edges_line.style("stroke-width", 1) })
                .on('click', (d, i) => { deleteLine(d, i); });

            const edges_text = svg.append("g").selectAll(".edgetext")
                .data(links)
                .enter()
                .append("text")
                .style("pointer-events", "none")
                .attr("class","linetext")
                .attr('text-anchor', "middle")
                .attr("fill-opacity", 1)
                .style("cursor","pointer")
                .attr({'class': 'edgelabel', 'id': function (d, i) { return 'edgepath' + i; }, 'dx': 50, 'dy': 0, 'fill':'#9aaabf'});

            //设置线条上的文字
            edges_text.append('textPath')
                .attr('xlink:href', function (d, i) { return '#edgepath' + i })
                .attr("pointer-events", "none").attr("font-size", 9)
                .text(function (d) { return d.relation.relationship; });

            const circle_g = svg.selectAll("circle")
                .data(force.nodes())//表示使用force.nodes数据
                .enter()
                .append("g")
                .attr("class",function(node,i){ return "g_circle_" + i; })//标记circle的父节点
                .style("cursor","pointer")
                .call(drag()) //将当前选中的元素传到drag函数中，使顶点可以被拖动
                .on("click", (node, i) => {
                    if (d3.event.defaultPrevented) return;//阻止click事件和拖拽事件冲突
                    circleClick(node, i, this);
                })
                .on('mouseover', (node) => {
                    if (d3.event.defaultPrevented) return;
                    showNodeInfo(node, this);

                    // showCircleBorderOuterArc(node, i);
                });

            svg.on("dblclick.zoom", null);//取消svg和圆圈的双击放大事件（d3中默认开启7个事件，关闭防止与上面的双击事件冲突）
            circle_g.on("dblclick.zoom", null);

            //圆圈
            const circle = circle_g.append("circle")
                .style("stroke-width", "2px")
                .attr("r", 25)//设置圆圈半径
                .style("fill", function (node) { return getCircleColor(node); });

            let text = circle_g.append("text")
                .attr("dy", ".35em")
                .attr("text-anchor", "middle")//在圆圈中加上数据
                .style('fill', "#FFFFFF")
                .attr('x',function(d){ appendCircleText(d, this); });

            //圆圈的提示文字
            circle.append("svg:title").text(function(node) {
                switch (node.entityLabel) {
                    case '原因': return node.entityName;
                    case '解决措施': return node.entityName;
                    case '现象': return node.entityName;
                    case '缺陷测试': return node.defectContent;
                    case 'IotInfrastructure': return node.entityName;
                    case 'DeviceAdmin': return node.name;

                }

            });
            text.append("svg:title").text(function(node) {
                switch (node.entityLabel) {
                    case '原因': return node.entityName;
                    case '解决措施': return node.entityName;
                    case '现象': return node.entityName;
                    case '缺陷测试': return node.defectContent;
                    case 'IotInfrastructure': return node.entityName;
                    case 'DeviceAdmin': return node.name;

                }

            });

            function zoomed() {//svg下的g标签移动大小
                svg.selectAll("g").attr("transform", "translate("  +d3.event.translate + ")scale(" +d3.event.scale + ")");
            }

            function getMarkerArrow(i) {
                svg.append("defs").append("marker")   //箭头
                    .attr("id", "arrow"+i)
                    .attr("markerUnits","strokeWidth")//设置为strokeWidth箭头会随着线的粗细发生变化
                    .attr("markerUnits", "userSpaceOnUse")
                    .attr("markerWidth", 10)//标识的大小
                    .attr("markerHeight", 10)
                    .attr("viewBox", "0 -4 12 15")//坐标系的区域
                    .attr("refX", 0)//箭头坐标
                    .attr("refY", 0)
                    .attr("orient", 'auto')//绘制方向，可设定为：auto（自动确认方向）和 角度值
                    .append("svg:path")
                    .attr("stroke-width",1)//箭头宽度
                    .attr("d", "M0,-5L10,0L0,5")//箭头的路径
                    .attr('fill', 'rgba(0,0,0, 0.4)');//箭头颜色
                return "url(#arrow" + i + ")";
            }

            function getStrokeWidth(d) {
                edges_line.style("stroke-width", function (edge) {
                    return edge.relation.relationshipId === d.relation.relationshipId ? 3 : 1;
                });
            }

            function drag(){//拖拽函数
                return force.drag().on("dragstart",function(d){
                    d3.event.sourceEvent.stopPropagation(); //取消默认事件
                    d.fixed = true;//拖拽开始后设定被拖拽对象为固定
                });
            }

            function circleClick(node, i, _this) {


                //获取当前节点是否包含圆环
                let existedRing = svg.select('.g_circle_' + i).selectAll('.g_circle_path');
                //清除上个节点的圆圈以及图标
                svg.selectAll('.g_circle_path').remove();
                svg.selectAll('.remove_a').remove();
                svg.selectAll('.hide_a').remove();
                svg.selectAll('.unlock_a').remove();
                svg.selectAll('.edit_a').remove();
                if(existedRing && existedRing[0].length === 0) {
                    //showCircleBorderOuterArc(node, i);
                    //绘制灰色可点击圆圈，分为三部分分别绘制
                    showRemove(node, i, _this);
                    showUnlock(node, i, _this);
                    showHide(node,i, _this);
                    showEdit(node,i, _this);
                }
            }

            function getCircleColor(node) {
                let color = {
                    '原因': "#FF9D00",
                    '现象': "#C477E9",
                    'Work': '#67CAF4',
                    '缺陷测试': '#BCDD73',
                    '解决措施': '#DD1E9E',
                    'Manufacturer': '#DDBA9E',
                    'IotInfrastructure': '#6ca5dd',
                    'DeviceAdmin': '#50DD87',
                };//圆圈背景色
                return color[node.entityLabel] || '#C477E9';
            }

            function appendCircleText(d, _this) {
                let circleText = '';
                if(d.entityLabel === '原因' && d.entityName) {
                    circleText = d.entityName;
                } else if(d.entityLabel === '现象') {
                    circleText = d.entityName;
                } else if (d.entityLabel === '解决措施') {
                    circleText = d.entityName;
                } else if (d.entityLabel === '缺陷测试') {
                    circleText = d.defectContent;
                } else if (d.entityLabel === 'IotInfrastructure') {
                    circleText = d.iotInfrastructureName;
                } else if (d.entityLabel === 'DeviceAdmin') {
                    circleText = d.name;
                }
              if(circleText && circleText.length > 10){
                  circleText = circleText.substring(0,10) + "...";
                  d3.select(_this).text(function(){return '';});
              }
              d3.select(_this)
                //返回缺失元素的占位对象（placeholder），指向绑定的数据中比选定元素集多出的一部分元素。

                .append("tspan")
                .attr("x", 0)//设置文字坐标
                .attr("y", 0)
                .style("fill","black")
                .attr("font-size", 12)
                .text(function(){ return circleText;
                });
                //如果小于四个字符，不换行
                // if(circleText && circleText.length > 4){
                //     circleText = circleText.substring(0,4) + "...";
                //     d3.select(_this).text(function(){return '';});
                // }
                // d3.select(_this).append('tspan').attr('x',0).attr('y',0).attr("font-size", 12)
                //     .text(function(){ return circleText; });
            }

            function deleteLine(d, i) {

                alert('delete this '+ d + ',' + i);
            }

            function tick() {
                circle.attr("transform", transform1);//圆圈
                text.attr("transform", transform2);//顶点文字
                edges_line.attr('d', function (d) {
                    //绘制自己指向自己的路径
                    if(d.relation.isSelf) { return getNodeSelfPath(d) }
                    //绘制两个节点之间的路径
                    return getNodesLine(d);//曲线路径
                });

                // if(circleBorderOuterArcObj !== null) {
                //   circleBorderOuterArcObj.attr("transform", function (d){ return "translate("+d.x+","+d.y+")" });
                // }

                //绘制节点删除功能半圆环
                if(removePath !== null) {
                    removePath.attr("transform", function (d){ return "translate("+d.x+","+d.y+")" });

                    let r1 = null;
                    remove_rect1.attr('d',function(d) { r1 = d; })
                        .attr("x",r1.x + 33 + 6.75).attr("y",r1.y  - 36 + 9.75).attr("width",5).attr("height",3).attr("rx",1.5).attr("ry",1.5);

                    let r2 = null;
                    remove_rect2.attr('d',function(d) { r2 = d; })
                        .attr("x",r2.x + 30 + 6.75).attr("y",r2.y  - 33 + 9.75).attr("width",12).attr("height",14).attr("rx",1.5).attr("ry",1.5);

                    let l1 = null;
                    remove_line1.attr('d',function(d) { l1 = d; })
                        .attr("x1",l1.x + 28 + 6.75).attr("y1",l1.y - 33 + 9.75).attr("x2",l1.x + 45 + 6.75).attr("y2",l1.y - 33 + 9.75);

                    let l2 = null;
                    remove_line2.attr('d',function(d) { l2 = d; })
                        .attr("x1",l2.x + 34 + 6.75).attr("y1",l2.y - 30 + 9.75).attr("x2",l2.x + 34 + 6.75).attr("y2",l2.y - 23 + 9.75);

                    let l3 = null;
                    remove_line3.attr('d',function(d) { l3 = d; })
                        .attr("x1",l3.x + 38 + 6.75).attr("y1",l3.y - 30 + 9.75).attr("x2",l3.x + 38 + 6.75).attr("y2",l3.y - 23 + 9.75);
                }

                //绘制解除节点位置锁定半圆环
                if(unlockPath !== null) {
                    unlockPath.attr("transform", function (d){ return "translate("+d.x+","+d.y+")" });
                    unlock_path1.attr("transform", function (d){ return "translate("+ (d.x - 45) +","+ (d.y - 23) +")" });

                    let r1 = null;
                    unlock_rect1.attr('d',function(d) { r1 = d; })
                        .attr("x",r1.x  - 52 + 6.75).attr("y",r1.y - 30 + 9.75).attr("width",15).attr("height",12).attr("rx",1.5).attr("ry",1.5);

                    let l1 = null;
                    unlock_line1.attr('d',function(d) { l1 = d; })
                        .attr("x1",l1.x  - 52 + 15).attr("y1",l1.y  - 30 + 15).attr("x2",l1.x  - 52 +15).attr("y2",l1.y  - 30 + 18);
                }

              if(editPath !== null) {
                editPath.attr("transform", function (d){ return "translate("+d.x+","+d.y+")" });
                edit_path1.attr("transform", function (d){ return "translate("+ (d.x - 45) +","+ (d.y - 23) +")" });

                let r1 = null;
                edit_rect1.attr('d',function(d) { r1 = d; })
                    .attr("x",r1.x  - 52 + 6.75).attr("y",r1.y - 30 + 9.75).attr("width",15).attr("height",12).attr("rx",1.5).attr("ry",1.5);

                let l1 = null;
                edit_line1.attr('d',function(d) { l1 = d; })
                    .attr("x1",l1.x  - 52 + 15).attr("y1",l1.y  - 30 + 15).attr("x2",l1.x  - 52 +15).attr("y2",l1.y  - 30 + 18);
              }

                //绘制隐藏节点和关系半圆环
                if(hidePath !== null) {
                    hidePath.attr("transform", function (d){ return "translate("+d.x+","+d.y+")" });

                    hide_path1
                        .attr("d", function(d) {
                            return "M" + (d.x - 13-20) + "," + (d.y + 42) + "A15,15,0,0,1" + (d.x + 13-20) + "," + (d.y + 42)
                        });
                    hide_path2
                        .attr("d", function(d) {
                            return "M"+ (d.x - 12 + 10.4-20) + "," + (d.y + 34 + 10.937) + "A" + 3.749 + "," + 3.749 + ",0,1,1," + (d.x - 12 + 15.338-20) + "," + (d.y + 33 + 9)
                        });
                    hide_path3
                        .attr("d", function(d) {
                            return "M" + (d.x - 12-20) + "," + (d.y + 42) + "A20,20,0,0,0" + (d.x - 4-20) + "," + (d.y + 47)
                        });
                    hide_path4
                        .attr("d", function(d) {
                            return "M" + (d.x + 11-20) + "," + (d.y + 44) + "A20,20,0,0,0" + (d.x + 13-20) + "," + (d.y + 42)
                        });

                    let c1 = null;
                    hide_circle1.attr('d',function(d) { c1 = d; })
                        .attr("cx",c1.x - 12 + 17.15-20).attr("cy",c1.y + 33 + 17.25).attr("r",6);

                    let l1 = null;
                    hide_line1.attr('d',function(d) { l1 = d; })
                        .attr("x1",l1.x - 12 + 14.15-20).attr("y1",l1.y + 33 + 17.25).attr("x2",l1.x - 12 + 20.15-20).attr("y2",l1.y + 33 + 17.25);

                }

            }

            //设置圆圈和文字的坐标
            function transform1(d) {
                return "translate(" + d.x + "," + d.y + ")";
            }

            function transform2(d) {
                return "translate(" + (d.x) + "," + d.y + ")";
            }
            function showRemove(node, i, _this) {
                removePath = d3.select('.g_circle_'+ i).append("path").attr('class', 'g_circle_path')
                    .attr("transform", "translate(" + node.x + "," + node.y + ")").attr("d", rightArc)
                    .attr("fill", "rgb(210, 213, 218)")
                    .style("cursor","pointer")
                    .on("click", () => { removeNodeAndPath(node, _this); })
                    .on("mouseover", function(){ removePath.attr("fill", "rgb(185, 185, 185)") })
                    .on("mouseout", function() { removePath.attr("fill", "rgb(210, 213, 218)") });

                d3.select('.g_circle_'+ i).append("g").attr("class","remove_a")
                    .attr("transform", "translate(" + (node.x + 33) + "," + (node.y - 24) + ") scale(0.7)")
                    .append("defs").append("style")
                    .text(".remove_a{fill:none;stroke:#FFFFFF;stroke-linecap:round;stroke-linejoin:round;stroke-width:1.5px;}");

                removePath.append("title").attr("class","remove_a").text("Remove");
                remove_rect1 = d3.select('.g_circle_'+ i).append("rect").attr("class","remove_a").attr("x",node.x + 33 + 6.75).attr("y",node.y  - 36 + 9.75)
                    .attr("width",5).attr("height",3).attr("rx",1.5).attr("ry",1.5);
                remove_rect2 = d3.select('.g_circle_'+ i).append("rect").attr("class","remove_a").attr("x",node.x + 30 + 6.75).attr("y",node.y  - 33 + 9.75)
                    .attr("width",12).attr("height",14).attr("rx",1.5).attr("ry",1.5);
                remove_line1 = d3.select('.g_circle_'+ i).append("line").attr("class","remove_a").attr("x1",node.x + 28 + 6.75).attr("y1",node.y - 33 + 9.75).attr("x2",node.x + 45 + 6.75).attr("y2",node.y - 33 + 9.75);
                remove_line2 = d3.select('.g_circle_'+ i).append("line").attr("class","remove_a").attr("x1",node.x + 34 + 6.75).attr("y1",node.y - 30 + 9.75).attr("x2",node.x + 34 + 6.75).attr("y2",node.y - 23 + 9.75);
                remove_line3 = d3.select('.g_circle_'+ i).append("line").attr("class","remove_a").attr("x1",node.x + 38 + 6.75).attr("y1",node.y - 30 + 9.75).attr("x2",node.x + 38 + 6.75).attr("y2",node.y - 23 + 9.75);

                d3.selectAll(".remove_a").on("click", () => { removeNodeAndPath(node, _this); })
            }

            function showHide(node,i) {
                hidePath = d3.select('.g_circle_'+ i).append("path").attr('class', 'g_circle_path')
                    .attr("transform", "translate(" + node.x + "," + node.y + ")").attr("d", bottomArc)
                    .attr("fill", "rgb(210, 213, 218)")
                    .style("cursor","pointer")
                    .on("click", function(){ hideNodeAndLinks(node) })
                    .on("mouseover", function(){ hidePath.attr("fill", "rgb(185, 185, 185)") })
                    .on("mouseout", function() { hidePath.attr("fill", "rgb(210, 213, 218)") });

                d3.select('.g_circle_'+ i).append("g").attr("class","hide_a")
                    .attr("transform", "translate(" + (node.x - 8-20) + "," + (node.y + 38) + ") scale(0.7)")
                    .append("defs").append("style")
                    .text(".hide_a{fill:none;stroke:#FFFFFF;stroke-linecap:round;stroke-linejoin:round;stroke-width:1.5px;}");

                hidePath.append("title").attr("class","hide_a").text("Hide");

                hide_path1 = d3.select('.g_circle_'+ i).append("path").attr("class","hide_a")
                    .attr("d", function(d) {
                        return "M" + (d.x - 13-20) + "," + (d.y + 42) + "A15,15,0,0,1" + (d.x + 13-20) + "," + (d.y + 42)
                    });
                hide_path2 = d3.select('.g_circle_'+ i).append("path").attr("class","hide_a")
                    .attr("d", function(d) {
                        return "M"+ (d.x - 12 + 10.4-20) + "," + (d.y + 34 + 10.937) + "A" + 3.749 + "," +  3.749 + ",0,1,1," + (d.x - 12 + 15.338-20) + "," + (d.y + 33 + 9)
                    });
                hide_path3 = d3.select('.g_circle_'+ i).append("path").attr("class","hide_a")
                    .attr("d", function(d) {
                        return "M" + (d.x - 12-20) + "," + (d.y + 42) + "A20,20,0,0,0" + (d.x - 4-20) + "," + (d.y + 47)
                    });
                hide_path4 = d3.select('.g_circle_'+ i).append("path").attr("class","hide_a")
                    .attr("d", function(d) {
                        return "M" + (d.x + 11-20) + "," + (d.y + 44) + "A20,20,0,0,0" + (d.x + 13-20) + "," + (d.y + 42)
                    });
                hide_circle1 = d3.select('.g_circle_'+ i).append("circle").attr("class","hide_a").attr("cx",node.x - 12 + 17.35-20).attr("cy",node.y + 33 + 17.25).attr("r",6);
                hide_line1 = d3.select('.g_circle_'+ i).append("line").attr("class","hide_a").attr("x1",node.x - 12 + 14.15-20).attr("y1",node.y + 33 + 17.25).attr("x2",node.x - 12 + 20.15-20).attr("y2",node.y + 33 + 17.25);
                d3.selectAll(".hide_a").on("click", (d) => { hideNodeAndLinks(d); })
            }
            function showUnlock(node, i,_this) {
                unlockPath = d3.select('.g_circle_'+ i).append("path").attr('class', 'g_circle_path')
                    .attr("transform", "translate(" + node.x + "," + node.y + ")").attr("d", leftArc)
                    .attr("fill", "rgb(210, 213, 218)").style("cursor","pointer")
                    .on("click", () => { editNodeFixed(node,_this); })

                    .on("mouseover", function(){ unlockPath.attr("fill", "rgb(185, 185, 185)") })
                    .on("mouseout", function() { unlockPath.attr("fill", "rgb(210, 213, 218)") });

                d3.select('.g_circle_'+ i).append("g").attr("class","unlock_a")
                    .attr("transform", "translate(" + (node.x - 52) + "," + (node.y - 30) + ") scale(0.7)")
                    .append("defs").append("style")
                    .text(".unlock_a{fill:none;stroke:#FFFFFF;stroke-linecap:round;stroke-linejoin:round;stroke-width:1.5px;}");

                unlockPath.append("title").attr("class","unlock_a").text("Edit");
                unlock_path1 = d3.select('.g_circle_'+ i).append("path").attr("class","unlock_a")
                    .attr("transform", "translate(" + (node.x - 45) + "," + (node.y - 23) + ")")
                    .attr("d", d3.svg.arc().outerRadius(5.3).innerRadius(5).startAngle(-1.8).endAngle(1.8))
                    .attr("fill", "#FFFFFF");

                unlock_rect1 = d3.select('.g_circle_'+ i).append("rect").attr("class","unlock_a").attr("x",node.x - 52 + 6.75).attr("y",node.y  - 30 + 9.75)
                    .attr("width",15).attr("height",12).attr("rx",1.5).attr("ry",1.5);
                unlock_line1 = d3.select('.g_circle_'+ i).append("line").attr("class","unlock_a").attr("x1",node.x  - 52 + 15).attr("y1",node.y - 30 + 15).attr("x2",node.x  - 52 + 15).attr("y2",node.y - 30 + 18);

                d3.selectAll(".unlock_a").on("click", (d) => { unlockNodeFixed(d); })

            }
          function showEdit(node, i) {
            editPath = d3.select('.g_circle_'+ i).append("path").attr('class', 'g_circle_path')
                .attr("transform", "translate(" + node.x + "," + node.y + ")").attr("d", editArc)
                .attr("fill", "rgb(210, 213, 218)").style("cursor","pointer")
                .on("click", function(d){ unlockNodeFixed(d); })
                .on("mouseover", function(){ editPath.attr("fill", "rgb(185, 185, 185)") })
                .on("mouseout", function() { editPath.attr("fill", "rgb(210, 213, 218)") });

            d3.select('.g_circle_'+ i).append("g").attr("class","edit_a")
                .attr("transform", "translate(" + (node.x - 52) + "," + (node.y - 30) + ") scale(0.7)")
                .append("defs").append("style")
                .text(".edit_a{fill:none;stroke:#FFFFFF;stroke-linecap:round;stroke-linejoin:round;stroke-width:1.5px;}");

            editPath.append("title").attr("class","edit_a").text("Unlock");
            edit_path1 = d3.select('.g_circle_'+ i).append("path").attr("class","edit_a")
                .attr("transform", "translate(" + (node.x - 45) + "," + (node.y - 23) + ")")
                .attr("d", d3.svg.arc().outerRadius(5.3).innerRadius(5).startAngle(-1.8).endAngle(1.8))
                .attr("fill", "#FFFFFF");

            edit_rect1 = d3.select('.g_circle_'+ i).append("rect").attr("class","edit_a").attr("x",node.x - 52 + 6.75).attr("y",node.y  - 30 + 9.75)
                .attr("width",15).attr("height",12).attr("rx",1.5).attr("ry",1.5);
            edit_line1 = d3.select('.g_circle_'+ i).append("line").attr("class","edit_a").attr("x1",node.x  - 52 + 15).attr("y1",node.y - 30 + 15).attr("x2",node.x  - 52 + 15).attr("y2",node.y - 30 + 18);

            d3.selectAll(".unlock_a").on("click", (d) => { unlockNodeFixed(d); })

          }
            function hideNodeAndLinks(node) {
                d3.event.stopPropagation();
                circle_g.attr('node', function(n) {
                    if(n.id === node.id) {
                        d3.select(this).remove();
                    }
                });
                edges_line.attr('d', function(d) {
                    if(d.source.id === node.id || d.target.id === node.id) {
                        d3.select(this).remove();
                    }
                });
            }

            function removeNodeAndPath(node, _this){
                d3.event.stopPropagation();
                //hideNodeAndLinks(node);//隐藏节点
                //alert('delete this '+ node + ',' + _this );

              _this.deletenode(node,node.id);
            }
            function showNodeInfo(node, _this) {
                //window.console.log(node);
                //window.console.log(_this);
                _this.tip=node;


            }
            function unlockNodeFixed(d) {
                d3.event.stopPropagation();
                d3.select(this).classed("fixed", d.fixed = false);//解除节点位置锁定
                //清除上个节点的圆圈以及图标
                svg.selectAll('.g_circle_path').remove();
                svg.selectAll('.remove_a').remove();
                svg.selectAll('.hide_a').remove();
                svg.selectAll('.unlock_a').remove();
            }
          function editNodeFixed(node,_this) {
            _this.EditDialogVisible=true;
            _this.val=node;
          }


        }

    }
}
</script>

<style>
.input-with-select {
  width: 600px;
  margin-right:450px;
  margin-left:15px;
}

.search-select {
  width: 120px;
}
.input-with-select .el-input-group__prepend {
  background-color: #fff;
}
.sub-navbar {
  height: 50px;
  line-height: 50px;
  position: relative;
  width: 100%;
  text-align: left;
  padding-right: 20px;
  -webkit-transition: 600ms ease position;
  transition: 600ms ease position;
  background: -webkit-gradient(linear, left top, right top, from(#c0ccda), color-stop(0%, #c0ccda), color-stop(100%, #c0ccda), to(#c0ccda));
  background: linear-gradient(90deg, #c0ccda 0%, #c0ccda 0%, #c0ccda 100%, #c0ccda 100%);
}
  #graph {
    width: 100vw;
    height: 100vh;
  }
</style>
