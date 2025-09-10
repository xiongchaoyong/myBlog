<template>
  <Nav />
  <SiderBar></SiderBar>
  <div class="contacts">
    <!-- 通知区域 -->
    <div class="notice">
      <div class="notice-header">
        <h3>
          通知
          <span style="font-size: 14px; color: #909399"
            >({{
              (receivedNotificationList?.length || 0) +
              (sentNotificationList?.length || 0)
            }})</span
          >
        </h3>
        <el-badge
          :value="unhandledNoticeCount"
          :hidden="unhandledNoticeCount === 0"
        >
          <el-icon>
            <Bell />
          </el-icon>
        </el-badge>
      </div>

      <div class="notice-content">
        <el-scrollbar height="calc(100vh - 190px)" class="custom-scrollbar">
          <div
            v-if="
              receivedNotificationList.length === 0 &&
              sentNotificationList.length === 0
            "
            class="empty-notice"
          >
            <el-icon class="empty-icon">
              <Bell />
            </el-icon>
            <p>暂无通知</p>
          </div>

          <div v-else class="notice-list">
            <!-- //receiver -->
            RECEIVE
            <div
              v-for="notice in receivedNotificationList"
              :key="notice.id"
              class="notice-item"
            >
              <div class="notice-avatar">
                <!-- <el-avatar :size="40" :src="notice.avatar" /> -->
                <Avatar
                  :avatar="notice.avatar"
                  :username="notice.username || '用户'"
                  :style="{ width: '50px', height: '50px', fontSize: '20px' }"
                />
              </div>
              <div class="notice-info">
                <div class="notice-title">
                  {{
                    notice.type === 1
                      ? "好友申请"
                      : notice.type === 2
                      ? "群聊邀请"
                      : "通知"
                  }}
                </div>
                <div class="notice-content-text">
                  {{ getNoticeContent(notice) }}
                </div>
                <div class="notice-time">
                  {{ formatDate(notice.createdAt, "datetime") }}
                </div>
              </div>
              <div class="notice-actions">
                <!-- 接收方 -->
                <template v-if="loginStore.user.id === notice.userId">
                  <el-button
                    v-if="notice.status === 0"
                    type="primary"
                    size="small"
                    @click="handleAcceptNotice(notice)"
                    >接受</el-button
                  >
                  <el-button
                    v-if="notice.status === 0"
                    type="danger"
                    size="small"
                    @click="handleRejectNotice(notice)"
                    >拒绝</el-button
                  >
                  <el-button
                    v-if="notice.status === 1"
                    type="success"
                    size="small"
                    disabled
                    >已同意</el-button
                  >
                  <el-button
                    v-if="notice.status === 2"
                    type="info"
                    size="small"
                    disabled
                    >已拒绝</el-button
                  >
                </template>
                <!-- 发送方 -->
                <template v-else-if="loginStore.user.id === notice.senderId">
                  <el-button
                    v-if="notice.status === 0"
                    type="warning"
                    size="small"
                    disabled
                    >申请中</el-button
                  >
                  <el-button
                    v-if="notice.status === 1"
                    type="success"
                    size="small"
                    disabled
                    >对方已同意</el-button
                  >
                  <el-button
                    v-if="notice.status === 2"
                    type="info"
                    size="small"
                    disabled
                    >对方已拒绝</el-button
                  >
                </template>
              </div>
            </div>

            SEND
            <div
              v-for="notice in sentNotificationList"
              :key="notice.id"
              class="notice-item"
            >
              <div class="notice-avatar">
                <el-avatar :size="40" :src="notice.avatar" />
              </div>
              <div class="notice-info">
                <div class="notice-title">
                  {{
                    notice.type === 1
                      ? "好友申请"
                      : notice.type === 2
                      ? "群聊邀请"
                      : "通知"
                  }}
                </div>
                <div class="notice-content-text">
                  {{ getNoticeContent(notice) }}
                </div>
                <div class="notice-time">
                  {{ formatDate(notice.createdAt, "datetime") }}
                </div>
              </div>
              <div class="notice-actions">
                <!-- 接收方 -->
                <template v-if="loginStore.user.id === notice.userId">
                  <el-button
                    v-if="notice.status === 0"
                    type="primary"
                    size="small"
                    @click="handleAcceptNotice(notice)"
                    >接受</el-button
                  >
                  <el-button
                    v-if="notice.status === 0"
                    type="danger"
                    size="small"
                    @click="handleRejectNotice(notice)"
                    >拒绝</el-button
                  >
                  <el-button
                    v-if="notice.status === 1"
                    type="success"
                    size="small"
                    disabled
                    >已同意</el-button
                  >
                  <el-button
                    v-if="notice.status === 2"
                    type="info"
                    size="small"
                    disabled
                    >已拒绝</el-button
                  >
                </template>
                <!-- 发送方 -->
                <template v-else-if="loginStore.user.id === notice.senderId">
                  <el-button
                    v-if="notice.status === 0"
                    type="warning"
                    size="small"
                    disabled
                    >申请中</el-button
                  >
                  <el-button
                    v-if="notice.status === 1"
                    type="success"
                    size="small"
                    disabled
                    >对方已同意</el-button
                  >
                  <el-button
                    v-if="notice.status === 2"
                    type="info"
                    size="small"
                    disabled
                    >对方已拒绝</el-button
                  >
                </template>
              </div>
            </div>
          </div>
        </el-scrollbar>
      </div>
    </div>

    <!-- 好友列表区域 -->
    <div class="friendList">
      <div class="friend-header">
        <h3>
          好友列表
          <span style="font-size: 14px; color: #909399"
            >({{ friendList?.length }})</span
          >
        </h3>
        <div class="search-add-container">
          <el-button type="primary" @click="showGroupManageDialog = true">
            <el-icon>
              <ChatDotRound />
            </el-icon>
            群聊管理
          </el-button>
          <el-button type="primary" @click="showAddFriendDialog = true">
            <el-icon>
              <Plus />
            </el-icon>
            添加好友
          </el-button>
          <el-input
            v-model="searchKeyword"
            placeholder="搜索好友"
            prefix-icon="Search"
            clearable
            style="width: 200px"
          />
        </div>
      </div>

      <div class="friend-content">
        <el-scrollbar height="calc(100vh - 190px)" class="custom-scrollbar">
          <div v-if="filteredFriendList?.length === 0" class="empty-friends">
            <el-icon class="empty-icon">
              <User />
            </el-icon>
            <p>{{ searchKeyword ? "未找到相关好友" : "暂无好友" }}</p>
          </div>

          <div v-else class="friend-list">
            <div
              v-for="(friend, index) in filteredFriendList"
              :key="friend.id"
              class="friend-item"
            >
              <div
                class="friend-card-content"
                :class="{ reverse: index % 2 === 1 }"
              >
                <!-- 头像区域 -->
                <div class="avatar-section">
                  <div class="avatar-container">
                    <Avatar
                      :avatar="friend.avatar"
                      :username="friend.username || '用户'"
                      :style="{
                        width: '120px',
                        height: '120px',
                        fontSize: '48px',
                      }"
                    />
                  </div>
                </div>

                <!-- 信息区域 -->
                <div class="info-section">
                  <el-descriptions direction="vertical" border :column="2">
                    <el-descriptions-item label="名称">{{
                      friend.username
                    }}</el-descriptions-item>
                    <el-descriptions-item label="性别">{{
                      friend.gender === 1
                        ? "男"
                        : friend.gender === 2
                        ? "女"
                        : "保密"
                    }}</el-descriptions-item>
                    <el-descriptions-item label="地址">{{
                      friend.region || "未设置"
                    }}</el-descriptions-item>
                    <el-descriptions-item label="加好友时间">{{
                      formatDate(friend.createdAt, "datetime")
                    }}</el-descriptions-item>
                    <el-descriptions-item label="个性签名">
                      {{ friend.signature || "这个人很懒，什么都没有留下~" }}
                    </el-descriptions-item>
                    <el-descriptions-item label="账号">
                      {{ friend.account || "这个人很懒，什么都没有留下~" }}
                    </el-descriptions-item>
                  </el-descriptions>
                </div>
              </div>

              <div class="friend-actions">
                <el-button type="primary" @click="handleChat(friend)">
                  <el-icon>
                    <ChatDotRound />
                  </el-icon>
                  聊天
                </el-button>
                <!-- <el-button type="success" @click="handleMoments(friend)">
                  <el-icon>
                    <Picture />
                  </el-icon>
                  朋友圈
                </el-button> -->
                <el-button type="danger" @click="handleDeleteFriend(friend)">
                  <el-icon>
                    <Delete />
                  </el-icon>
                  删除好友
                </el-button>
              </div>
            </div>
          </div>
        </el-scrollbar>
      </div>
    </div>
  </div>

  <!-- 添加好友弹窗 -->
  <el-dialog
    v-model="showAddFriendDialog"
    title="添加好友"
    width="500px"
    @close="handleCloseDialog"
  >
    <el-form :model="addFriendForm" label-width="80px" @submit.prevent>
      <el-form-item label="账号">
        <el-input
          v-model="addFriendForm.account"
          placeholder="请输入要查找的账号"
          clearable
          @keyup.enter="handleSearchUser"
        />
      </el-form-item>
    </el-form>

    <!-- 查找结果 -->
    <div
      v-if="searchResult && searchResult.status !== 'failed'"
      class="search-result"
    >
      <el-divider content-position="left">查找结果</el-divider>
      <div class="user-info">
        <el-avatar :size="60" :src="searchResult.avatar" />
        <div class="user-details">
          <div class="username">{{ searchResult.username }}</div>
          <div class="account">账号：{{ searchResult.account }}</div>
          <div class="signature">
            {{ searchResult.signature || "这个人很懒，什么都没有留下~" }}
          </div>
          <div class="user-actions">
            <el-button
              v-if="searchResult && searchResult.status === 'pending'"
              type="primary"
              @click="handleSendFriendRequest()"
            >
              发送好友申请
            </el-button>
            <el-button
              v-else-if="searchResult && searchResult.status === 'applied'"
              type="success"
              disabled
            >
              已申请
            </el-button>
            <el-button
              v-else-if="searchResult && searchResult.status === 'friend'"
              type="info"
              disabled
            >
              已是好友
            </el-button>
            <el-button
              v-else-if="searchResult && searchResult.status === 'self'"
              type="info"
              disabled
            >
              本人
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 未找到结果 -->
    <div v-else-if="hasSearched && !searchResult" class="search-result">
      <el-divider content-position="left">查找结果</el-divider>
      <div class="no-result">
        <el-icon class="no-result-icon">
          <User />
        </el-icon>
        <p>未找到该用户，请检查账号是否正确</p>
      </div>
    </div>

    <!-- 初始状态 -->
    <div v-else class="search-result">
      <el-divider content-position="left">查找结果</el-divider>
      <div class="initial-state">
        <el-icon class="initial-icon">
          <Search />
        </el-icon>
        <p>请输入账号进行查找</p>
      </div>
    </div>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="showAddFriendDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSearchUser">查找</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 群聊管理弹窗 -->
  <el-dialog
    v-model="showGroupManageDialog"
    title="群聊管理"
    width="80%"
    @close="handleCloseGroupDialog"
  >
    <div class="group-manage-container">
      <!-- 顶部操作栏 -->
      <div class="group-manage-header">
        <div class="header-left">
          <el-button type="primary" @click="showCreateGroupDialog = true">
            <el-icon>
              <Plus />
            </el-icon>
            创建群聊
          </el-button>
          <el-button type="success" @click="openJoinGroupDialog()">
            <el-icon>
              <UserFilled />
            </el-icon>
            加入群聊
          </el-button>
        </div>
      </div>

      <!-- 群聊列表 -->
      <div class="group-list-section">
        <el-tabs v-model="activeGroupTab" class="group-tabs">
          <el-tab-pane label="我创建的群聊" name="created">
            <div class="group-list">
              <div v-if="myCreatedGroups.length === 0" class="empty-groups">
                <el-icon class="empty-icon">
                  <ChatDotRound />
                </el-icon>
                <p>您还没有创建任何群聊</p>
              </div>
              <div v-else class="group-grid">
                <div
                  v-for="group in myCreatedGroups"
                  :key="group.id"
                  class="group-card"
                >
                  <el-button
                    class="dissolve-btn"
                    type="danger"
                    size="small"
                    @click="handleGroupCommand({ action: 'dissolve', group })"
                  >
                    <el-icon>
                      <Delete />
                    </el-icon>
                    解散
                  </el-button>
                  <div class="group-card-header">
                    <div class="group-avatar">
                      <img
                        :src="group.groupAvatar || '@/assets/img/avatar.png'"
                        alt="群聊头像"
                      />
                    </div>
                    <div class="group-info">
                      <h4>{{ group.groupName }}</h4>
                      <p class="group-number">群号：{{ group.groupAccount }}</p>
                      <p>{{ group.memberCount }}人</p>
                    </div>
                  </div>
                  <div class="group-card-content">
                    <p class="group-time">
                      创建时间：{{ formatDate(group.createdAt, "datetime") }}
                    </p>
                  </div>
                  <div class="group-card-actions">
                    <el-button
                      type="primary"
                      size="small"
                      @click="handleGroupChat(group)"
                    >
                      <el-icon>
                        <ChatDotRound />
                      </el-icon>
                      进入群聊
                    </el-button>
                    <el-button
                      type="success"
                      size="small"
                      @click="handleGroupCommand({ action: 'edit', group })"
                    >
                      <el-icon>
                        <Edit />
                      </el-icon>
                      编辑
                    </el-button>
                    <el-button
                      type="warning"
                      size="small"
                      @click="handleGroupCommand({ action: 'members', group })"
                    >
                      <el-icon>
                        <User />
                      </el-icon>
                      成员
                    </el-button>
                    <el-button
                      class="invite-btn"
                      size="small"
                      @click="handleGroupCommand({ action: 'invite', group })"
                    >
                      <el-icon>
                        <Plus />
                      </el-icon>
                      邀请
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="我加入的群聊" name="joined">
            <div class="group-list">
              <div v-if="myJoinedGroups.length === 0" class="empty-groups">
                <el-icon class="empty-icon">
                  <ChatDotRound />
                </el-icon>
                <p>您还没有加入任何群聊</p>
              </div>
              <div v-else class="group-grid">
                <div
                  v-for="group in myJoinedGroups"
                  :key="group.id"
                  class="group-card"
                >
                  <div class="group-card-header">
                    <div class="group-avatar">
                      <img
                        :src="group.groupAvatar || '@/assets/img/avatar.png'"
                        alt="群聊头像"
                      />
                    </div>
                    <div class="group-info">
                      <h4>{{ group.groupName }}</h4>
                      <p class="group-number">群号：{{ group.groupAccount }}</p>
                      <p>{{ group.memberCount }}人</p>
                    </div>
                  </div>
                  <div class="group-card-content">
                    <p class="group-time">
                      创建时间:{{ formatDate(group.createdAt, "datetime") }}
                    </p>
                  </div>
                  <div class="group-card-actions">
                    <el-button
                      type="primary"
                      size="small"
                      @click="handleGroupChat(group)"
                    >
                      <el-icon>
                        <ChatDotRound />
                      </el-icon>
                      进入群聊
                    </el-button>
                    <el-button
                      type="warning"
                      size="small"
                      @click="handleGroupCommand({ action: 'members', group })"
                    >
                      <el-icon>
                        <User />
                      </el-icon>
                      成员
                    </el-button>
                    <el-button
                      type="danger"
                      size="small"
                      @click="handleGroupCommand({ action: 'leave', group })"
                    >
                      <el-icon>
                        <Close />
                      </el-icon>
                      退出
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </el-dialog>

  <!-- 创建群聊弹窗 -->
  <el-dialog
    v-model="showCreateGroupDialog"
    title="创建群聊"
    width="600px"
    @close="handleCloseCreateGroupDialog"
  >
    <div class="create-group-container">
      <div class="create-group-form">
        <el-form
          :model="createGroupForm"
          :rules="createGroupRules"
          ref="createGroupFormRef"
          label-width="80px"
        >
          <el-form-item label="群聊头像">
            <div class="avatar-upload-container">
              <el-upload
                class="avatar-uploader"
                :show-file-list="false"
                :before-upload="beforeUpload"
                :on-success="handleAvatarUploadSuccesss"
                accept="image/*"
                :action="`${uploadUrl}/user/upload`"
                :headers="headers"
              >
                <img
                  v-if="createGroupForm.groupAvatar"
                  :src="createGroupForm.groupAvatar"
                  class="avatar-preview"
                />
                <el-icon v-else class="avatar-uploader-icon">
                  <Plus />
                </el-icon>
              </el-upload>

              <div class="avatar-tip">点击上传群聊头像</div>
            </div>
          </el-form-item>
          <el-form-item label="群聊名称" prop="name">
            <el-input
              v-model="createGroupForm.name"
              placeholder="请输入群聊名称"
              maxlength="20"
              show-word-limit
            />
          </el-form-item>
          <el-form-item label="群号" prop="groupNumber">
            <el-input
              v-model="createGroupForm.groupNumber"
              placeholder="请输入群号"
              maxlength="10"
              show-word-limit
            />
            <div class="form-tip">群号用于其他用户搜索加入</div>
          </el-form-item>
          <el-form-item label="选择成员">
            <div class="member-selector">
              <div class="friend-selection-list">
                <el-checkbox-group v-model="createGroupForm.selectedMembers">
                  <div
                    v-for="friend in friendList"
                    :key="friend.friendId"
                    class="friend-selection-item"
                  >
                    <el-checkbox :label="friend.friendId">
                      <div class="friend-selection-info">
                        <el-avatar
                          :size="30"
                          :src="friend.avatar || '../assets/img/avatar.png'"
                        />
                        <span class="friend-selection-name">{{
                          friend.username
                        }}</span>
                      </div>
                    </el-checkbox>
                  </div>
                </el-checkbox-group>
              </div>
              <div class="selected-members">
                <p>已选择：{{ createGroupForm.selectedMembers.length }}人</p>
              </div>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="showCreateGroupDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreateGroup"
          >创建群聊</el-button
        >
      </span>
    </template>
  </el-dialog>

  <!-- 群成员管理弹窗 -->
  <el-dialog v-model="showMembersDialog" title="群成员管理" width="700px">
    <div class="member-container">
      <div class="members-header">
        <h4>
          {{ currentGroup?.groupName }} - 成员列表 ({{
            groupMembers?.length
          }}人)
        </h4>
      </div>
      <div class="member-list">
        <div
          v-for="member in groupMembers"
          :key="member.id"
          class="member-item"
        >
          <div class="member-info">
            <el-avatar
              :size="40"
              :src="member.avatar || '../assets/img/avatar.png'"
            />
            <div class="member-details">
              <div class="member-name">
                {{ member.username }}
                <el-tag
                  v-if="member.id === currentGroup?.creatorId"
                  size="small"
                  type="success"
                  >群主</el-tag
                >
                <el-tag
                  v-else-if="member.id === loginStore.user.id"
                  size="small"
                  type="info"
                  >我</el-tag
                >
              </div>
              <div class="member-meta">
                <div class="member-time">
                  加入时间：{{ formatDate(member.joinedAt, "datetime") }}
                </div>
                <div class="member-account">账号：{{ member.account }}</div>
              </div>
            </div>
          </div>
          <div class="member-actions">
            <el-button
              v-if="isGroupCreator && member.id !== loginStore.user.id"
              type="danger"
              size="small"
              @click="handleKickMember(member)"
            >
              <el-icon>
                <Delete />
              </el-icon>
              踢出
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </el-dialog>

  <!-- 邀请成员弹窗 -->
  <el-dialog v-model="showInviteDialog" title="邀请成员" width="600px">
    <div class="invite-container">
      <div class="invite-user-list">
        <div
          v-for="user in usersForInvite"
          :key="user.id"
          class="invite-user-item"
        >
          <div class="invite-user-info">
            <el-avatar
              :size="40"
              :src="user.avatar || '../assets/img/avatar.png'"
            />
            <div class="invite-user-details">
              <div class="invite-user-name">{{ user.username }}</div>
              <div class="invite-user-account">账号：{{ user.account }}</div>
            </div>
          </div>
          <el-button
            v-if="!user.invited"
            class="invite-btn"
            size="small"
            @click="handleInviteUser(user)"
          >
            <el-icon>
              <Plus />
            </el-icon>
            邀请
          </el-button>
          <el-button v-else type="success" size="small" disabled>
            已邀请
          </el-button>
        </div>
      </div>
    </div>
  </el-dialog>

  <!-- 编辑群信息弹窗 -->
  <el-dialog v-model="showEditGroupDialog" title="编辑群信息" width="500px">
    <el-form
      :model="editGroupForm"
      :rules="editGroupRules"
      ref="editGroupFormRef"
      label-width="80px"
    >
      <el-form-item label="群聊头像">
        <div class="avatar-upload-container">
          <el-upload
            class="avatar-uploader"
            :show-file-list="false"
            :before-upload="beforeUpload"
            :on-success="handleAvatarUploadSuccess"
            accept="image/*"
            :action="`${uploadUrl}/user/upload`"
            :headers="headers"
          >
            <img
              v-if="editGroupForm.groupAvatar"
              :src="editGroupForm.groupAvatar"
              class="avatar-preview"
            />
            <el-icon v-else class="avatar-uploader-icon">
              <Plus />
            </el-icon>
          </el-upload>
          <div class="avatar-tip">点击修改群聊头像</div>
        </div>
      </el-form-item>
      <el-form-item label="群聊名称" prop="name">
        <el-input
          v-model="editGroupForm.name"
          placeholder="请输入群聊名称"
          maxlength="20"
          show-word-limit
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="cancleUpload">取消</el-button>
        <el-button type="primary" @click="handleUpdateGroup">保存</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 加入群聊弹窗 -->
  <el-dialog
    v-model="showJoinGroupDialog"
    title="加入群聊"
    width="600px"
    @close="handleCloseJoinGroupDialog"
    class="join-group-dialog"
  >
    <div class="join-group-container">
      <el-form :model="joinGroupForm" label-width="80px" @submit.prevent>
        <el-form-item label="群号">
          <el-input
            v-model="joinGroupForm.searchKeyword"
            placeholder="请输入群号"
            clearable
            @keyup.enter="handleSearchGroup"
          />
        </el-form-item>
      </el-form>

      <!-- 搜索结果 -->
      <div v-if="groupSearchResult" class="search-result-section">
        <el-divider content-position="left">搜索结果</el-divider>
        <div class="group-search-item">
          <div class="group-search-info">
            <el-avatar
              :size="50"
              :src="groupSearchResult.groupAvatar || '../assets/img/avatar.png'"
            />
            <div class="group-search-details">
              <div class="group-search-name">
                {{ groupSearchResult.groupName }}
              </div>
              <div class="group-search-number">
                群号：{{ groupSearchResult.groupAccount }}
              </div>
              <div class="group-search-members">
                {{ groupSearchResult.memberCount }}人
              </div>
              <div class="group-search-creator">
                群主：{{ groupSearchResult.creatorName }}
              </div>
            </div>
          </div>
          <div class="group-search-actions">
            <el-button
              v-if="groupSearchResult.status === 'notJoined'"
              type="primary"
              size="small"
              @click="handleJoinGroup(groupSearchResult)"
            >
              申请加入
            </el-button>
            <el-button
              v-else-if="groupSearchResult.status === 'pending'"
              type="warning"
              size="small"
              disabled
            >
              申请中
            </el-button>
            <el-button
              v-else-if="groupSearchResult.status === 'joined'"
              type="success"
              size="small"
              disabled
            >
              已是成员
            </el-button>
            <el-button
              v-else-if="groupSearchResult.status === 'created'"
              type="info"
              size="small"
              disabled
            >
              我创建的
            </el-button>
          </div>
        </div>
      </div>

      <!-- 未找到结果 -->
      <div
        v-else-if="
          hasGroupSearched &&
          (!groupSearchResult || groupSearchResult.length === 0)
        "
        class="search-result-section"
      >
        <el-divider content-position="left">搜索结果</el-divider>
        <div class="no-group-result">
          <el-icon class="no-result-icon">
            <ChatDotRound />
          </el-icon>
          <p>未找到相关群聊，请检查群号是否正确</p>
        </div>
      </div>

      <!-- 初始状态 -->
      <div v-else class="search-result-section">
        <el-divider content-position="left">搜索结果</el-divider>
        <div class="initial-group-state">
          <el-icon class="initial-icon">
            <Search />
          </el-icon>
          <p>请输入群号进行搜索</p>
        </div>
      </div>
    </div>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="showJoinGroupDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSearchGroup">搜索</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, onMounted, watch, reactive } from "vue";
import {
  Bell,
  User,
  Search,
  ChatDotRound,
  Delete,
  Picture,
  Plus,
  CircleCloseFilled,
  MoreFilled,
  Edit,
  View,
  Close,
  UserFilled,
} from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import Friend from "@/api/Friend";
import { useUserStore } from "@/stores/userStore";
import { useFriendStore } from "@/stores/friendStore";
import Notification from "@/api/Notification";
import formatDate from "@/utils/timeFormate";
import { useNotificationStore } from "@/stores/notificationStore";
import PrivateMessage from "@/api/PrivateMessage";
import { useMessageStore } from "@/stores/messageStore";
import { useRouter } from "vue-router";
import { useGroupChatStore } from "@/stores/groupChatStore";
import Upload from "@/api/Upload";
import GroupChat from "@/api/GroupChat";
import GroupMember from "@/api/GroupMember";
import GroupMessage from "@/api/GroupMessage";
import { useWsChatStore } from "@/stores/wsChatStore";
import { useGroupMessageStore } from "@/stores/groupMessageStore";
import { usePostStore } from "@/stores/postStroe";
import request from "@/utils/request";
import Avatar from "./Avatar.vue";
import UserApi from "@/api/User";
import Nav from "@/views/socialMeida/components/Nav.vue";
import SiderBar from "@/components/SiderBar.vue";

const uploadUrl = import.meta.env.VITE_SERVER_URL;

//store
const loginStore = useUserStore();
const friendStore = useFriendStore();
const notificationStore = useNotificationStore();
const messageStore = useMessageStore();
const groupChatStore = useGroupChatStore();
const wsChatStore = useWsChatStore();
const groupMessageStore = useGroupMessageStore();
const postStore = usePostStore();

// 群聊管理相关
//群聊管理相关弹窗开关状态
const showGroupManageDialog = ref(false); //群聊管理
const showCreateGroupDialog = ref(false); //创建群聊
const showMembersDialog = ref(false); //成员
const showInviteDialog = ref(false); //邀请成员
const showEditGroupDialog = ref(false); //编辑群
const showJoinGroupDialog = ref(false); //加入群聊

//当前激活的群聊标签页
const activeGroupTab = ref("created");

//创建群聊表单
const createGroupForm = ref({
  name: "",
  groupNumber: "",
  selectedMembers: [],
  groupAvatar: "",
});

//编辑群聊表单
const editGroupForm = ref({
  id: null,
  name: "",
  groupAvatar: "",
});

//表单验证规则
const createGroupRules = {
  name: [
    { required: true, message: "请输入群聊名称", trigger: "blur" },
    { min: 1, max: 20, message: "群聊名称长度在1到20个字符", trigger: "blur" },
  ],
  groupNumber: [
    { required: true, message: "请输入群号", trigger: "blur" },
    { min: 1, max: 10, message: "群号长度在1到10个字符", trigger: "blur" },
  ],
};

const editGroupRules = {
  name: [
    { required: true, message: "请输入群聊名称", trigger: "blur" },
    { min: 1, max: 20, message: "群聊名称长度在1到20个字符", trigger: "blur" },
  ],
};

//表单引用
const createGroupFormRef = ref(null);
const editGroupFormRef = ref(null);

//我创建的群聊
const myCreatedGroups = computed(() => groupChatStore.getMyCreatedGroups || []);

//我加入的群聊
const myJoinedGroups = computed(() => groupChatStore.getMyJoinedGroups || []);

onMounted(async () => {
  //主动刷新，记得带上await
  if(!groupChatStore.getMyCreatedGroups )
  await groupChatStore.fetchMyCreatedGroups();
  await groupChatStore.fetchMyJoinedGroups();
});
onMounted(async () => {
  //确保好数据已加载
  await notificationStore.fetchNoticeList();
  const res = await friendStore.fetchFriendList();
  if (res?.data.code === 1) {
    friendStore.setFriendList(res?.data.data);
  }
  
});

onMounted(async () => {
  //确保好数据已加载
  await notificationStore.fetchNoticeList();
  const res = await friendStore.fetchFriendList();
  if (res?.data.code === 1) {
    friendStore.setFriendList(res?.data.data);
  }
});
//群成员列表
const groupMembers = ref([]);

//当前选中的群聊
const currentGroup = ref(null);

//可邀请的用户列表
const usersForInvite = ref([]);

//是否为群主
const isGroupCreator = computed(() => {
  if (!currentGroup.value) return false;
  return currentGroup.value.creatorId === loginStore.user.id;
});

//处理群聊命令
const handleGroupCommand = async ({ action, group }) => {
  currentGroup.value = group;
  switch (action) {
    case "edit":
      showEditGroupDialog.value = true;
      editGroupForm.value = {
        id: group.id,
        name: group.groupName,
        groupAvatar: group.groupAvatar,
      };
      break;
    case "members":
      showMembersDialog.value = true;
      //获取群成员
      await groupChatStore.fetchGroupMembers(currentGroup.value.id);
      groupMembers.value = groupChatStore.getGroupMembers;
      break;
    case "invite":
      showInviteDialog.value = true;
      // 模拟获取可邀请用户
      await groupChatStore.fetchGroupMembers(currentGroup.value.id);
      groupMembers.value = groupChatStore.getGroupMembers;
      const invitableFriends = getInvitableFriends(
        friendList.value,
        groupMembers.value
      );
      usersForInvite.value = invitableFriends.map((friend) => ({
        ...friend,
        invited: false,
      }));
      break;
    case "dissolve":
      handleDissolveGroup(group);
      break;
    case "leave":
      handleLeaveGroup(group);
      break;
  }
};

//邀请成员时展示的成员列表，好友列表中不能有在群里面的
const getInvitableFriends = (friendList, groupMembers) => {
  console.log(groupMembers);
  const memberIds = new Set(groupMembers.map((member) => member.id));
  return friendList.filter((friend) => !memberIds.has(friend.friendId));
};

//处理创建群聊,点击创建
const handleCreateGroup = async () => {
  if (!createGroupFormRef.value) return;

  try {
    await createGroupFormRef.value.validate();

    if (createGroupForm.value.selectedMembers.length === 0) {
      ElMessage.warning("请至少选择一名成员");
      return;
    }

    // 调用api
    const res = await GroupChat.createGroup({
      groupName: createGroupForm.value.name,
      groupAccount: createGroupForm.value.groupNumber,
      memberIds: createGroupForm.value.selectedMembers,
      creatorid: loginStore.user.id,
      groupAvatar: createGroupForm.value.groupAvatar,
    });

    if (res.data.code == 1) {
      ElMessage.success("群聊创建成功");
      //主动刷新
      groupChatStore.fetchMyCreatedGroups();
    }
    showCreateGroupDialog.value = false;
    handleCloseCreateGroupDialog();
  } catch (error) {
    console.error("创建群聊失败:", error);
  }
};

//修改群聊名称、头像
const handleUpdateGroup = async () => {
  if (!editGroupFormRef.value) return;

  try {
    await editGroupFormRef.value.validate();

    // 这里调用更新群聊API
    const res = await GroupChat.updateGroup(editGroupForm.value);
    if (res.data.code == 1) ElMessage.success("群聊信息更新成功");
    //主动刷新
    groupChatStore.fetchMyCreatedGroups();
    showEditGroupDialog.value = false;
  } catch (error) {
    console.error("更新群聊信息失败:", error);
  }
};

//处理邀请用户
const handleInviteUser = async (user) => {
  if (user.invited) return; // 防止重复点击
  user.invited = true; // 立即更新UI

  try {
    //邀请,就是发送通知
    const res = await Notification.sendRequest({
      groupId: currentGroup.value.id,
      content: currentGroup.value.groupName,
      userId: user.friendId,
      type: 2,
      senderId: loginStore.user.id,
      status: 0,
    });
    if (res.data.code == 1) {
      ElMessage.success(`已邀请 ${user.username} 加入群聊`);
      notificationStore.fetchNoticeList();
    } else {
      ElMessage.error("邀请失败");
    }
  } catch (error) {
    ElMessage.error("邀请失败");
    console.error("邀请用户失败:", error);
  }
};

//踢出群成员
const handleKickMember = async (member) => {
  try {
    console.log("handleKickMember", member);
    await ElMessageBox.confirm(
      `确定要将 ${member.username} 踢出群聊吗？`,
      "确认踢出",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );
    const res = await GroupMember.deleteMember({
      groupId: currentGroup.value.id,
      userId: member.id,
    });

    if (res.data.code == 1) {
      ElMessage.success("成员已踢出");
      await groupChatStore.fetchGroupMembers(currentGroup.value.id);
      groupMembers.value = groupChatStore.getGroupMembers;
      groupChatStore.fetchMyCreatedGroups();
      //这里如果不主动刷新的话，你点击成员(点击成员就主动刷新了成员列表)看到的是更新后的成员，但是人数还是原来的i
    }
  } catch (error) {
    if (error !== "cancel") {
      console.error("踢出成员失败:", error);
    }
  }
};
//处理退出群聊
const handleLeaveGroup = async (group) => {
  try {
    await ElMessageBox.confirm(
      `确定要退出群聊"${group.groupName}"吗？`,
      "确认退出",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );
    const res = await GroupMember.deleteMember({
      groupId: group.id,
      userId: loginStore.user.id,
    });
    if (res.data.code == 1) {
      ElMessage.success("已退出群聊");
      groupChatStore.fetchGroupMembers(group.id);
      groupChatStore.fetchMyJoinedGroups();
    }
  } catch (error) {
    if (error !== "cancel") {
      console.error("退出群聊失败:", error);
    }
  }
};
//处理解散群聊
const handleDissolveGroup = async (group) => {
  try {
    await ElMessageBox.confirm(
      `确定要解散群聊"${group.groupName}"吗？解散后无法恢复。`,
      "确认解散",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );

    // 这里调用解散群聊API
    const res = await GroupChat.dissolveGroup(group.id);
    if (res.data.code == 1) {
      ElMessage.success("群聊已解散");
      //主动刷新
      groupChatStore.fetchMyCreatedGroups();
    }
  } catch (error) {
    if (error !== "cancel") {
      console.error("解散群聊失败:", error);
    }
  }
};

/**
 * 处理进入群聊
 */
const handleGroupChat = (group) => {
  ElMessage.success(`进入群聊：${group.groupName}`);
  // 这里可以跳转到群聊页面
  // router.push(`/group-chat/${group.id}`)
};

/**
 * 处理关闭群聊管理弹窗
 */
const handleCloseGroupDialog = () => {
  showGroupManageDialog.value = false;
  // groupSearchKeyword.value = ''
  activeGroupTab.value = "created";
};

/**
 * 处理关闭创建群聊弹窗
 */
const handleCloseCreateGroupDialog = () => {
  createGroupForm.value = {
    name: "",
    groupNumber: "",
    selectedMembers: [],
    groupAvatar: "",
  };
  // memberSearchKeyword.value = ''
  if (createGroupFormRef.value) {
    createGroupFormRef.value.resetFields();
  }
};

// 加入群聊相关
/**
 * 加入群聊表单
 */

const openJoinGroupDialog = () => {
  showJoinGroupDialog.value = true;
};
const joinGroupForm = ref({
  searchKeyword: "",
});

/**
 * 搜索结果
 */
const groupSearchResult = ref(null);

/**
 * 是否已经搜索过
 */
const hasGroupSearched = ref(false);

/**
 * 搜索群聊
 */
const handleSearchGroup = async () => {
  if (!joinGroupForm.value.searchKeyword.trim()) {
    ElMessage.warning("请输入要搜索的内容");
    return;
  }

  try {
    hasGroupSearched.value = true;
    const res = await GroupChat.searchGroup(joinGroupForm.value.searchKeyword);
    if (res.data.code == 1) {
      ElMessage.success("搜索成功");
      groupSearchResult.value = res.data.data || null;
    } else {
      ElMessage.error("搜索失败");
      groupSearchResult.value = null;
    }
  } catch (error) {
    hasGroupSearched.value = true;
    groupSearchResult.value = [];
    ElMessage.error("搜索失败，请检查输入是否正确");
  }
};

/**
 * 处理加入群聊
 */
const handleJoinGroup = async (group) => {
  // 这里应该调用实际的API
  console.log("加入群聊", group);
  const res = await Notification.sendRequest({
    groupId: group.id,
    content: group.groupName,
    userId: group.creatorId,
    type: 3, //3代表群聊申请
    senderId: loginStore.user.id,
    status: 0,
  });
  if (res.data.code === 1) {
    ElMessage.success(`已成功申请加入群聊：${group.groupName}`);
    notificationStore.fetchNoticeList();
  }

  showJoinGroupDialog.value = false;
};

/**
 * 处理关闭加入群聊弹窗
 */
const handleCloseJoinGroupDialog = () => {
  joinGroupForm.value = {
    searchKeyword: "",
  };
  groupSearchResult.value = [];
  hasGroupSearched.value = false;
  groupSearchResult.value = null;
};

//图像上传

//上传限制
// 限制文件大小和类型
const beforeUpload = (file) => {
  const isImage = file.type === "image/jpeg" || file.type === "image/png";
  const isLt20M = file.size / 1024 / 1024 < 20;

  if (!isImage) {
    ElMessage.error("只能上传 JPG/PNG 格式的图片");
    return false;
  }
  if (!isLt20M) {
    ElMessage.error("上传文件大小不能超过 20MB");
    return false;
  }

  return true;
};
const avatarOssUrl = ref();
const handleAvatarUploadSuccess = (response, file, fileList) => {
  editGroupForm.value.groupAvatar = ref(response.data); //响应式
  avatarOssUrl.value = response.data.split("/").pop();
};
const handleAvatarUploadSuccesss = (response, file, fileList) => {
  createGroupForm.value.groupAvatar = ref(response.data); //响应式
  avatarOssUrl.value = response.data.split("/").pop();
};
//upload的action设置请求头token
const token = localStorage.getItem("token") || "";

const headers = {
  token: `${token}`,
};

const cancleUpload = () => {
  showEditGroupDialog.value = false;
  //把上传到oss的图片删除
  if (avatarOssUrl.value) {
    Upload.deleteUpload(avatarOssUrl.value);
    editGroupForm.value.avatar = userInfo.value.avatar;
  }
};

//插件
const router = useRouter();
// 搜索好友关键词
const searchKeyword = ref("");
const myId = loginStore.user.id;
// 通知列表数据渲染
const receivedNotificationList = computed(
  () => notificationStore.getReceivedNotificationList || []
);
const sentNotificationList = computed(
  () => notificationStore.getSentNotificationList || []
);

// 1. 恢复friendList为computed写法
const friendList = computed(() => friendStore.getFriendList || []);



// 根据搜索好友关键词得到的列表
const filteredFriendList = computed(() => {
  if (!searchKeyword.value) {
    return friendList.value;
  }
  return friendList.value.filter(
    (friend) =>
      friend.username
        .toLowerCase()
        .includes(searchKeyword.value.toLowerCase()) ||
      friend.region
        ?.toLowerCase()
        .includes(searchKeyword.value.toLowerCase()) ||
      friend.signature
        ?.toLowerCase()
        .includes(searchKeyword.value.toLowerCase())
  );
});

//查找和添加好友
// 添加好友弹窗
const showAddFriendDialog = ref(false);

//添加好友表单输入的数据
const addFriendForm = ref({
  account: "",
});

//根据输入的好友账号获得的查找数据
const searchResult = ref(null);

//是否输入，代表查找过
const hasSearched = ref(false);

//点击查找用户逻辑处理
const handleSearchUser = async () => {
  if (!addFriendForm.value.account.trim()) {
    ElMessage.warning("请输入要查找的账号");
    return;
  }

  try {
    hasSearched.value = true;

    // 调用api
    const response = await UserApi.searchByAccount(addFriendForm.value.account);
    console.log(response);
    searchResult.value = response.data.data;
    if (searchResult.value && searchResult.value.status !== "failed") {
      ElMessage.success("查找成功");
    } else {
      ElMessage.warning("未找到该用户");
    }
  } catch (error) {
    hasSearched.value = true;
    searchResult.value = null;
    ElMessage.error("查找失败，请检查账号是否正确");
  }
};

//添加好友
//查找到后点击发送好友申请
const handleSendFriendRequest = async () => {
  // 插入一条好友申请通知
  const res = await Notification.sendRequest({
    userId: searchResult.value.id,
    type: 1, //1代表好友申请
    senderId: loginStore.user.id,
    status: 0,
  });
  if (res.data.code === 1) {
    ElMessage.success("发送成功");
    searchResult.value.status = "applied";
    // 更新列表,这里可以强制刷新，调用数据库
    notificationStore.fetchNoticeList();
  } else {
    ElMessage.warning("发送失败");
  }
};
//关闭添加好友弹窗
const handleCloseDialog = () => {
  // 重置表单
  addFriendForm.value = {
    account: "",
  };
  // 重置搜索结果
  searchResult.value = null;
  // 重置搜索状态
  hasSearched.value = false;
};

//处理通知
//通知列表中处理，处理同意
const handleAcceptNotice = async (notice) => {
  notice.status = 1;
  //更新通知表、好友表(主动再获取一次数据，前端就刷新了)
  if (notice.type === 1) {
    const res = await Notification.updateStatus({
      //其实这里传入id和status就可以，但是为时已晚
      senderId: notice.senderId,
      userId: notice.userId,
      status: 1,
      type: notice.type,
    });
    if (res.data.code == 1) {
      const ress = await Friend.addFriend({
        userId: notice.userId,
        friendId: notice.senderId,
      });
      if (ress.data.code == 1) {
        ElMessage.success("已添加好友");
        //主动刷新
        friendStore.fetchFriendList();
        const content =
          loginStore.user.id === notice.senderId
            ? "你好,我是" + loginStore.user.username
            : "你好,我是" + notice.username;
        //添加打招呼的信息,发送方给接收方私信
        const res = await PrivateMessage.sendGreetMessage({
          senderId: notice.senderId,
          receiverId: notice.userId,
          content: content,
        });
        if (res.data.code == 1) {
          messageStore.fetchMesList();
          messageStore.fetchMesListAll();
        }
      }
    }
  }
  //处理群聊邀请通知的同意(这部分后端逻辑本应该一起写的，同一个事务)
  if (notice.type == 2) {
    //更新通知表
    const res = await Notification.updateStatus({
      //其实这里传入id和status就可以，但是为时已晚
      senderId: notice.senderId,
      userId: notice.userId,
      status: 1,
      type: notice.type,
    });
    if (res.data.code == 1) {
      const ress = await GroupMember.addMember({
        userId: notice.userId,
        groupId: notice.groupId,
      });
      if (ress.data.code == 1) {
        ElMessage.success("已加入群聊!");
        //主动刷新
        groupChatStore.fetchMyJoinedGroups();
        const content = "大家好,我是" + loginStore.user.username;
        //添加打招呼的信息,发送方给接收方私信
        const res = await GroupMessage.sendGreetMessage({
          senderId: loginStore.user.id,
          groupId: notice.groupId,
          content: content,
        });
        const mes = {
          content: content,
          senderId: loginStore.user.id,
          groupId: notice.groupId,
          senderAvatar: loginStore.user.avatar,
          senderName: loginStore.user.username,
          type: "group",
          messageTime: Date.now(),
        };
        wsChatStore.sendMessage(mes);
      }
    }
  }
  //处理群聊申请的通知
  if (notice.type == 3) {
    //更新通知表
    const res = await Notification.updateStatus({
      //其实这里传入id和status就可以，但是为时已晚
      senderId: notice.senderId,
      userId: notice.userId,
      status: 1,
      type: notice.type,
    });
    if (res.data.code == 1) {
      const ress = await GroupMember.addMember({
        userId: notice.senderId,
        groupId: notice.groupId,
      });
      if (ress.data.code == 1) {
        ElMessage.success("已同意!");
        //主动刷新
        groupChatStore.fetchMyCreatedGroups();
        const content = "大家好,我是" + notice.username;
        //添加打招呼的信息,发送方给接收方私信
        const res = await GroupMessage.sendGreetMessage({
          senderId: notice.senderId,
          groupId: notice.groupId,
          content: content,
        });
       
      }
    }
  }
};

//处理拒绝
const handleRejectNotice = async (notice) => {
  notice.status = 2;
  const res = await Notification.updateStatus({
    //其实这里传入id和status就可以，但是为时已晚
    senderId: notice.senderId,
    userId: notice.userId,
    status: 2,
    type: notice.type,
  });
  if (res.data.code == 1) ElMessage.warning("已拒绝");
};

//计算待处理的通知数量，根据status=0
const unhandledNoticeCount = computed(() => {
  return (
    receivedNotificationList.value.filter(
      (n) => n.status === 0 && n.userId === myId
    ).length +
    sentNotificationList.value.filter(
      (n) => n.status === 0 && n.senderId === myId
    ).length
  );
});

// 根据通知类型、身份和用户名动态生成通知内容
const getNoticeContent = (notice) => {
  if (notice.type === 1) {
    // 好友申请
    if (notice.userId === myId) {
      return `${notice.username} 请求添加您为好友`;
    } else if (notice.senderId === myId) {
      return `您请求添加 ${notice.username} 为好友`;
    }
  } else if (notice.type === 2) {
    // 群聊邀请
    if (notice.userId === myId) {
      return `${notice.username} 邀请您加入群聊  ` + `"${notice.content}"`;
    } else if (notice.senderId === myId) {
      return `您邀请 ${notice.username} 加入群聊  ` + `"${notice.content}"`;
    }
  } else if (notice.type === 3) {
    // 群聊申请
    if (notice.userId === myId) {
      return `${notice.username} 申请加入群聊  ` + `"${notice.content}"`;
    } else if (notice.senderId === myId) {
      return `您申请加入群聊  ` + `"${notice.content}"`;
    }
  }
  return "";
};

//好友信息的三个按钮

//点击聊天按钮，跳转
const handleChat = (friend) => {
  //根据当前id和好友id得到在meslist中的索引
  messageStore.jumpFromContacts(loginStore.user.id, friend.friendId);
  //跳转到聊天页面
  router.push("/chat");
};
//点击朋友圈按钮，跳转
function getMomentsListById(friendId) {
  return request({
    method: "get",
    url: `user/post/getMomentsListById/${friendId}`,
  });
}
const handleMoments = async (friend) => {
  const res = await getMomentsListById(friend.friendId);
  postStore.setMomentsList(res.data.data);
  ElMessage.success(`查看 ${friend.username} 的朋友圈`);
  //传递路由参数
  router.push({
    path: "/moments",
    query: {
      friendId: friend.friendId,
    },
  });
  // router.push("/moments");
};

//点击删除按钮，删除
const handleDeleteFriend = async (friend) => {
  ElMessageBox.confirm(`确定要删除好友 ${friend.username} 吗？`, "确认删除", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      //删除好友关系表 friend.id    userId  双向删除
      await Friend.deleteFriend({
        userId: loginStore.user.id,
        friendId: friend.friendId,
      });
      //删除通知表
      await Notification.deleteNotification({
        userId: loginStore.user.id,
        senderId: friend.friendId,
      });
      //删除相关的聊天信息
      await PrivateMessage.deleteMesList(friend.friendId);
      //主动刷新
      await friendStore.fetchFriendList();
      await notificationStore.fetchNoticeList();
      ElMessage.success("删除成功");
    })
    .catch(() => {
      ElMessage.info("已取消删除");
    });
};
</script>

<style scoped lang="scss">
.el-dialog__body {
  height: 300px !important;
  min-height: 300px;
  max-height: 300px;
  overflow: hidden;
  padding: 0;
}

.contacts {
  display: flex;
  height: 91vh;
  background-color: #f5f5f5;
  overflow: hidden;
  margin-left: 60px;
}

.notice {
  width: 30%;
  height: 100vh;
  background-color: white;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.notice-header {
  padding: 20px;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;

  h3 {
    margin: 0;
    color: #303133;
    font-size: 18px;
  }
}

.notice-content {
  flex: 1;
  padding: 0;
}

.notice-list {
  padding: 10px;
}

.notice-item {
  display: flex;
  align-items: flex-start;
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.3s;
  position: relative;

  &:hover {
    background-color: #f5f7fa;
  }

  &:last-child {
    border-bottom: none;
  }
}

.notice-avatar {
  margin-right: 12px;
  flex-shrink: 0;
}

.notice-info {
  flex: 1;
  min-width: 0;
}

.notice-title {
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
  font-size: 14px;
}

.notice-content-text {
  color: #606266;
  font-size: 13px;
  margin-bottom: 4px;
  line-height: 1.4;
}

.notice-time {
  color: #909399;
  font-size: 12px;
}

.notice-actions {
  display: flex;
  flex-direction: column;
  gap: 5px;
  position: absolute;
  right: 20px;
  bottom: 15px;
  margin-left: 0;
}

.empty-notice {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #909399;

  .empty-icon {
    font-size: 48px;
    margin-bottom: 10px;
  }

  p {
    margin: 0;
    font-size: 14px;
  }
}

.friendList {
  width: 70%;
  height: 100vh;
  background-color: white;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.friend-header {
  padding: 20px;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;

  h3 {
    margin: 0;
    color: #303133;
    font-size: 18px;
  }
}

.search-add-container {
  display: flex;
  gap: 10px;
  align-items: center;
}

.search-result {
  margin-top: 20px;
}

.user-info {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 15px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 8px;
  position: relative;
  min-height: 100px;
}

.user-details {
  flex: 1;
  min-width: 0;
}

.username {
  font-weight: 600;
  color: #303133;
  font-size: 16px;
  margin-bottom: 5px;
}

.account {
  color: #606266;
  font-size: 14px;
  margin-bottom: 5px;
}

.signature {
  color: #909399;
  font-size: 13px;
  line-height: 1.4;
}

.user-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: flex-end;
  position: absolute;
  right: 20px;
  bottom: 20px;
}

.friend-content {
  flex: 1;
  padding: 0;
  width: 100%;
}

.friend-list {
  padding: 20px;
  width: 100%;
  box-sizing: border-box;
}

.friend-item {
  margin-bottom: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  transition: box-shadow 0.3s;
  width: 100%;
  box-sizing: border-box;

  &:hover {
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  }

  &:last-child {
    margin-bottom: 0;
  }
}

.friend-card-content {
  display: flex;
  align-items: flex-start;
  padding: 20px;
  gap: 30px;
  min-height: 200px;
  width: 100%;
  box-sizing: border-box;

  &.reverse {
    flex-direction: row-reverse;
  }
}

.avatar-section {
  flex-shrink: 0;
  width: 200px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.info-section {
  flex: 1;
  min-width: 0;
}

.avatar-container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 200px;
  height: 200px;
}

.avatar-placeholder {
  width: 120px;
  height: 120px;
  border: 2px dashed #d9d9d9;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: #fafafa;
  color: #8c939d;
}

.avatar-placeholder-icon {
  font-size: 28px;
  margin-bottom: 10px;
}

.avatar-placeholder-text {
  font-size: 12px;
}

.friend-actions {
  padding: 15px;
  background-color: #fafafa;
  display: flex;
  gap: 10px;
  justify-content: center;
  border-top: 1px solid #e4e7ed;
}

.empty-friends {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #909399;

  .empty-icon {
    font-size: 48px;
    margin-bottom: 10px;
  }

  p {
    margin: 0;
    font-size: 14px;
  }
}

/* 自定义滚动条样式 - 隐藏滚动条 */
.custom-scrollbar {
  width: 100%;

  :deep(.el-scrollbar__bar) {
    opacity: 0;
    transition: opacity 0.3s;
  }

  &:hover {
    :deep(.el-scrollbar__bar) {
      opacity: 0.3;
    }
  }

  :deep(.el-scrollbar__wrap) {
    width: 100%;
  }

  :deep(.el-scrollbar__view) {
    width: 100%;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .contacts {
    flex-direction: column;
  }

  .notice,
  .friendList {
    width: 100%;
    height: 50vh;
  }

  .notice {
    border-right: none;
    border-bottom: 1px solid #e4e7ed;
  }
}

.no-result {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: #909399;

  .no-result-icon {
    font-size: 48px;
    margin-bottom: 15px;
    color: #c0c4cc;
  }

  p {
    margin: 0;
    font-size: 14px;
  }
}

.initial-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: #909399;

  .initial-icon {
    font-size: 48px;
    margin-bottom: 15px;
    color: #c0c4cc;
  }

  p {
    margin: 0;
    font-size: 14px;
  }
}

/* 群聊管理样式 */
.group-manage-container {
  height: 70vh;
  display: flex;
  flex-direction: column;
}

.group-manage-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
  border-bottom: 1px solid #e4e7ed;
  margin-bottom: 20px;
}

.group-tabs {
  flex: 1;
  overflow: hidden;
}

.group-list {
  height: 100%;
  overflow: hidden;
}

.group-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  padding: 20px 0;
  height: calc(100% - 40px);
  overflow-y: auto;
}

.group-card {
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: relative;

  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    transform: translateY(-2px);
  }
}

.dissolve-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  z-index: 10;
}

.group-card-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  position: relative;
}

.group-info {
  flex: 1;
  margin-left: 15px;

  h4 {
    margin: 0 0 5px 0;
    font-size: 16px;
    color: #303133;
  }

  .group-number {
    margin: 0 0 5px 0;
    font-size: 12px;
    color: #409eff;
    font-weight: 500;
  }

  p {
    margin: 0;
    font-size: 14px;
    color: #909399;
  }
}

.group-card-content {
  margin-bottom: 15px;

  .group-time {
    margin: 0;
    font-size: 12px;
    color: #909399;
  }
}

.group-card-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}

.group-card-actions .el-button {
  flex: 1;
  min-width: 60px;
  max-width: 80px;
}

.empty-groups {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #909399;

  .empty-icon {
    font-size: 48px;
    margin-bottom: 15px;
    color: #c0c4cc;
  }

  p {
    margin: 0;
    font-size: 14px;
  }
}

/* 创建群聊样式 */
.member-selector {
  .friend-list-for-group {
    max-height: 200px;
    overflow-y: auto;
    border: 1px solid #e4e7ed;
    border-radius: 4px;
    margin: 10px 0;
    padding: 10px;
  }

  .friend-item-for-group {
    padding: 8px 0;
    border-bottom: 1px solid #f0f0f0;

    &:last-child {
      border-bottom: none;
    }
  }

  .friend-info-for-group {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .selected-members {
    text-align: center;
    color: #909399;
    font-size: 14px;
  }
}

/* 群成员管理样式 */
.members-container {
  height: 60vh;
  display: flex;
  flex-direction: column;
}

.members-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 0 20px 0;
  border-bottom: 1px solid #e4e7ed;
  margin-bottom: 20px;

  h4 {
    margin: 0;
    color: #303133;
  }
}

.members-list {
  flex: 1;
  overflow-y: auto;
}

.member-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    background-color: #f5f7fa;
  }
}

.member-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.member-details {
  .member-name {
    font-weight: 600;
    color: #303133;
    margin-bottom: 5px;
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .member-meta {
    display: flex;
    gap: 15px;
    align-items: center;
  }

  .member-time {
    font-size: 12px;
    color: #909399;
  }

  .member-account {
    font-size: 12px;
  }

  color: #909399;

  .member-actions {
    display: flex;
    gap: 10px;
  }
}

/* 邀请成员样式 */
.invite-container {
  height: 50vh;
  display: flex;
  flex-direction: column;
}

.invite-user-list {
  flex: 1;
  overflow-y: auto;
  margin-top: 20px;
}

.invite-user-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    background-color: #f5f7fa;
  }
}

.invite-user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.invite-user-details {
  .invite-user-name {
    font-weight: 600;
    color: #303133;
    margin-bottom: 5px;
  }

  .invite-user-account {
    font-size: 12px;
    color: #909399;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .group-grid {
    grid-template-columns: 1fr;
  }

  .group-manage-header {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }

  .member-item,
  .invite-user-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .member-actions {
    align-self: flex-end;
  }
}

/* 加入群聊样式 */
.join-group-container {
  height: 40vh;
  display: flex;
  flex-direction: column;
}

.search-result-section {
  flex: 1;
  overflow-y: auto;
  margin-top: 20px;
}

.group-search-list {
  max-height: 400px;
  overflow-y: auto;
}

.group-search-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    background-color: #f5f7fa;
  }
}

.group-search-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.group-search-details {
  .group-search-name {
    font-weight: 600;
    color: #303133;
    margin-bottom: 5px;
    font-size: 16px;
  }

  .group-search-number {
    font-size: 12px;
    color: #409eff;
    margin-bottom: 5px;
    font-weight: 500;
  }

  .group-search-members {
    font-size: 12px;
    color: #909399;
  }

  .group-search-creator {
    font-size: 12px;
    color: #909399;
  }
}

.group-search-actions {
  display: flex;
  gap: 10px;
}

.no-group-result,
.initial-group-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: #909399;

  .no-result-icon,
  .initial-icon {
    font-size: 48px;
    margin-bottom: 15px;
    color: #c0c4cc;
  }

  p {
    margin: 0;
    font-size: 14px;
  }
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
  line-height: 1.4;
}

/* 更新header样式 */
.group-manage-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
  border-bottom: 1px solid #e4e7ed;
  margin-bottom: 20px;
}

.header-left {
  display: flex;
  gap: 10px;
  align-items: center;
}

/* 群聊管理弹窗样式 */
.group-manage-container {
  height: 70vh;
  display: flex;
  flex-direction: column;
}

.group-list-section {
  flex: 1;
  overflow-y: auto;
  margin-top: 20px;
}

.group-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  padding: 10px;
}

.group-card {
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    transform: translateY(-2px);
  }
}

.group-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.group-info {
  flex: 1;
  margin-right: 15px;
}

.group-name {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
  line-height: 1.3;
}

.group-number {
  font-size: 14px;
  color: #409eff;
  font-weight: 500;
  margin-bottom: 8px;
}

.group-meta {
  display: flex;
  gap: 15px;
  font-size: 12px;
  color: #909399;
}

.group-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.group-action-btn {
  padding: 6px 12px;
  font-size: 12px;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
  background: white;
  color: #606266;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    border-color: #409eff;
    color: #409eff;
  }

  &.primary {
    background: #409eff;
    color: white;
    border-color: #409eff;

    &:hover {
      background: #66b1ff;
    }
  }

  &.danger {
    background: #f56c6c;
    color: white;
    border-color: #f56c6c;

    &:hover {
      background: #f78989;
    }
  }
}

/* 成员管理样式 */
.member-container {
  height: 60vh;
  display: flex;
  flex-direction: column;
}

.member-list {
  flex: 1;
  overflow-y: auto;
  margin-top: 20px;
}

.member-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    background-color: #f5f7fa;
  }
}

.member-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.member-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #e1f3d8;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  color: #67c23a;
}

.member-details {
  .member-name {
    font-weight: 600;
    color: #303133;
    margin-bottom: 5px;
  }

  .member-meta {
    display: flex;
    gap: 15px;
    align-items: center;
  }

  .member-time {
    font-size: 12px;
    color: #909399;
  }
}

.member-actions {
  display: flex;
  gap: 10px;
}

/* 邀请成员样式 */
.invite-container {
  height: 60vh;
  display: flex;
  flex-direction: column;
}

.invite-user-list {
  flex: 1;
  overflow-y: auto;
  margin-top: 20px;
}

.invite-user-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    background-color: #f5f7fa;
  }
}

.invite-user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.invite-user-details {
  .invite-user-name {
    font-weight: 600;
    color: #303133;
    margin-bottom: 5px;
  }

  .invite-user-account {
    font-size: 12px;
    color: #909399;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .group-grid {
    grid-template-columns: 1fr;
  }

  .group-manage-header {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }

  .member-item,
  .invite-user-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .member-actions {
    align-self: flex-end;
  }
}

/* 加入群聊样式 */
.join-group-container {
  height: 70vh;
  display: flex;
  flex-direction: column;
}

.search-result-section {
  flex: 1;
  overflow-y: auto;
  margin-top: 20px;
}

.group-search-list {
  max-height: 500px;
  overflow-y: auto;
}

/* 创建群聊样式 */
.create-group-container {
  display: flex;
  flex-direction: column;
}

.create-group-form {
  /* flex: 1; */
  /* overflow-y: auto; */
  margin-top: 20px;
}

.friend-selection-list {
  width: 300px;
  max-height: 200px;
  /* 或者你认为更合适的高度 */
  overflow-y: auto;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  margin-top: 10px;
}

.friend-selection-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 15px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.3s ease;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    background-color: #f5f7fa;
  }

  &.selected {
    background-color: #e1f3d8;
    border-color: #67c23a;
  }
}

.friend-selection-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.friend-selection-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #e1f3d8;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  color: #67c23a;
  font-size: 14px;
}

.friend-selection-name {
  font-weight: 500;
  color: #303133;
}

.friend-selection-status {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #67c23a;

  &.offline {
    background: #c0c4cc;
  }
}

/* 头像上传样式 */
.avatar-upload-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 100px;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: border-color 0.3s;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}

.avatar-preview {
  width: 100px;
  height: 100px;
  object-fit: cover;
  display: block;
}

.avatar-tip {
  font-size: 12px;
  color: #909399;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .group-grid {
    grid-template-columns: 1fr;
  }

  .group-manage-header {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }

  .member-item,
  .invite-user-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .member-actions {
    align-self: flex-end;
  }
}

.invite-btn {
  --el-button-bg-color: rgb(158, 116, 214);
  --el-button-border-color: rgb(158, 116, 214);
  --el-button-hover-bg-color: rgb(182, 149, 226);
  --el-button-hover-border-color: rgb(182, 149, 226);
  --el-button-active-bg-color: rgb(138, 96, 194);
  --el-button-active-border-color: rgb(138, 96, 194);
  --el-button-text-color: #ffffff;
}

.friend-selection-status {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #67c23a;

  &.offline {
    background: #c0c4cc;
  }
}

/* 群聊头像样式 */
.group-avatar {
  width: 50px;
  height: 50px;
  border-radius: 4px;
  overflow: hidden;
  flex-shrink: 0;
  background-color: #f0f2f5;
}

.group-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 头像上传样式 */
.avatar-upload-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 100px;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: border-color 0.3s;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}

.avatar-preview {
  width: 100px;
  height: 100px;
  object-fit: cover;
  display: block;
}

.avatar-tip {
  font-size: 12px;
  color: #909399;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .group-grid {
    grid-template-columns: 1fr;
  }

  .group-manage-header {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }

  .member-item,
  .invite-user-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .member-actions {
    align-self: flex-end;
  }
}

/* 只针对加入群聊弹窗，限制内容高度 */
.join-group-dialog .el-dialog__body {
  height: 30vh !important;
  min-height: 0 !important;
  max-height: 30vh !important;
  overflow: hidden !important;
  padding: 0 !important;
}

.join-group-dialog .join-group-container {
  height: 100%;
  max-height: 100%;
  overflow-y: auto;
  box-sizing: border-box;
  padding: 20px;
}
</style>
