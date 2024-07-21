<template>
  <div style="border: 4px solid orange;
              border-radius: 4px;
              padding: 10px; ">

  </div>
  <a-divider style="border-color: #7cb305" dashed />
  <div style="border: 4px solid #99CCFF;
              border-radius: 4px;
              padding: 10px; ">
    <h3>
      <b>示例网站只开放会员端功能，控台管理暂不开放（怕被乱改车次数据，影响体验）</b>
    </h3>
    <div>
      如果您还没有乘车人，可以先去
      <router-link to="/passenger">
        添加乘车人
      </router-link>
    </div>
    <div>
      购票功能支持选座，去
      <router-link to="/ticket">
        查询余票并购票
      </router-link>
      。购票成功后，可以查看
      <router-link to="/my-ticket">
        我的车票
      </router-link>
      ；去
      <router-link to="/seat">
        座位销售图
      </router-link>
      查看每个座位的销售情况
    </div>
  </div>

</template>

<script>

import { defineComponent } from 'vue';

export default defineComponent({
  name: "welcome-view",
  setup() {
    return {
    };
  },
});
</script>
