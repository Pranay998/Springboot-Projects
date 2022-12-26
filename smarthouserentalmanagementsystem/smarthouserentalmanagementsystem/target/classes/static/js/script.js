console.log("Hello");

const toggleSidebar=()=>{
    if($(".sidebar").is(":visible")){

        $(".sidebar").cs("display","none");
        $(".content").css("margin-left","0%");
    }else{
        $(".sidebar").cs("display","block");
        $(".content").css("margin-left","20%");
    }
};




const search = () =>{
  let query=$("#search-input").val();
   if(query=="")
   {
    $(".search-result").hide();

   }else{ 
    //search
    console.log(query);

    //sending request to server
let url=`http://localhost://8080/search/${query}`;
fetch(url).then((response)=>{
    return response.json();
})
.then((data)=>{
console.log(data);

let text=`<div class='list-group'>`;

	data.foreach((booking)=>{
    text += `<a href = '#' class= 'list-group-item list-group-action'>$(booking.name}</a>`
});
text += `</div>`;
$(".search-result").html(text);
$(".search-result").show();

});
 
   }
};