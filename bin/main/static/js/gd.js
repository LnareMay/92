async function uploadToServer (formObj) {
    console.log(formObj)
    const response = await axios({
        method: 'post',
        url: '/club/upload',
        data: formObj,
        headers: {
            'Content-Type': 'multipart/form-data',
        },
    });
    return response.data
}

async function removeFileToServer(uuid, fileName){
    const response = await axios.delete(`/club/remove/${uuid}_${fileName}`)
    return response.data
}

async function get1(bno) {
	const result = await axios.get(`/replies/list/${bno}`)
	return result;
}

async function getList({boardNo, clubCode, page, size, goLast}) {
	const result = await axios.get(`/club/replies/list/${boardNo},${clubCode}`, {params: {boardNo, clubCode, page, size}})
	
	if(goLast){
	    const total = result.data.total
	    const lastPage = parseInt(Math.ceil(total/size))
	    return getList({boardNo:boardNo, clubCode:clubCode, page:lastPage, size:size})
	}    	
	
	return result.data
}

async function addReply(replyObj) {
	const response = await axios.post(`/club/replies/register/`, replyObj)
	return response.data
}


async function getReply(clubCode, boardNo, replyNo) {
	const response = await axios.get(`club/replies/getReply${clubCode},${boardNo},${replyNo}`)
	return response.data
}

async function modifyReply(replyObj) {
	const response = await axios.post(`/replies/modify${replyObj}`, replyObj)
	return response.data
}

async function removeReply(clubCode, boardNo, replyNo) {
	const response = await axios.delete(`/replies/deleteReply${clubCode, boardNo, replyNo}`)
	return response.data
}
