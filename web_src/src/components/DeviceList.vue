<template>
  <div id="app" style="width: 100%">
    <div class="page-header">
      <div class="page-title">设备列表</div>
    </div>
    <!--设备列表-->
    <el-table :data="deviceList" style="width: 100%;font-size: 12px;" :height="winHeight" header-row-class-name="table-header">
      <el-table-column prop="name" label="名称" min-width="160">
      </el-table-column>
      <el-table-column prop="deviceId" label="设备编号" min-width="200" >
      </el-table-column>
      <el-table-column label="地址" min-width="160" >
        <template slot-scope="scope">
          <div slot="reference" class="name-wrapper">
            <el-tag size="medium">{{ scope.row.hostAddress }}</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="manufacturer" label="厂家" min-width="120" >
      </el-table-column>
      <el-table-column prop="channelCount" label="通道数" min-width="120" >
      </el-table-column>
      <el-table-column label="状态" min-width="120">
        <template slot-scope="scope">
          <div slot="reference" class="name-wrapper">
            <el-tag size="medium" v-if="scope.row.online == 1">在线</el-tag>
            <el-tag size="medium" type="info" v-if="scope.row.online == 0">离线</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" min-width="450" fixed="right">
        
      </el-table-column>
    </el-table>
    <el-pagination
      style="float: right"
      @size-change="handleSizeChange"
      @current-change="currentChange"
      :current-page="currentPage"
      :page-size="count"
      :page-sizes="[15, 25, 35, 50]"
      layout="total, sizes, prev, pager, next"
      :total="total">
    </el-pagination>
  </div>
</template>

<script>
import uiHeader from '../layout/UiHeader.vue'

export default {
  name: 'app',
  components: {
    uiHeader,
  },
  data() {
    return {
      deviceList: [], //设备列表
      currentPage: 1,
      count: 15,
      total: 0,
    };
  },
  computed: {
  },
  mounted() {
    this.initData();
  },
  destroyed() {

  },
  methods: {
    initData: function () {
      this.getDeviceList();
    },
    getDeviceList: function () {
      this.getDeviceListLoading = true;
      this.$axios({
        method: 'get',
        url: `/api/device/query/devices`,
        params: {
          page: this.currentPage,
          count: this.count
        }
      }).then( (res)=> {
        if (res.data.code === 0) {
          this.total = res.data.data.total;
          this.deviceList = res.data.data.list;
        }
        this.getDeviceListLoading = false;
      }).catch( (error)=> {
        console.error(error);
        this.getDeviceListLoading = false;
      });
    },
  }
};
</script>

<style>
.videoList {
  display: flex;
  flex-wrap: wrap;
  align-content: flex-start;
}

.video-item {
  position: relative;
  width: 15rem;
  height: 10rem;
  margin-right: 1rem;
  background-color: #000000;
}

.video-item-img {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  margin: auto;
  width: 100%;
  height: 100%;
}

.video-item-img:after {
  content: "";
  display: inline-block;
  position: absolute;
  z-index: 2;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  margin: auto;
  width: 3rem;
  height: 3rem;
  background-image: url("../assets/loading.png");
  background-size: cover;
  background-color: #000000;
}

.video-item-title {
  position: absolute;
  bottom: 0;
  color: #000000;
  background-color: #ffffff;
  line-height: 1.5rem;
  padding: 0.3rem;
  width: 14.4rem;
}

</style>
