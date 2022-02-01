$(document).ready(function(){
	$(function () {
	
		var table = $('#searchResults').DataTable({
			"lengthMenu": [
				[ 10, 25, 50, 100, 200, 1000, -1 ],
				[ 10, 25, 50, 100, 200, 1000, "All" ] ],
			"pageLength": 10,			
			"dom": "<'row'<'col-sm-3'i><'col-sm-2'l><'col-sm-3'f><'col-sm-2'B>>"+"<'row'<'col-sm-12't>> "+"<'row'<'col-sm-5'i><'col-sm-7'p>>",
			buttons: ['colvis'  
				/*{extend:'excel',text: 'Export To Excel',
				exportOptions: {												
								modifier: {page: 'current'}
				}}*/
			],
			language: {
				search: "Search Results:",
				buttons: {colvis: 'Show/Hide Columns'}
			},
			colReorder: true,
	        fixedHeader: true,
			initComplete: function () {				
				//Input box for all fields
				this.api().columns().every( function () {
					var column = this;
					var select = $('<input type="text"  />')
					.appendTo( $(column.footer()).empty() )
					.on( 'keyup change', function () { 
						column
						.search( $(this).val(), true, false)
						.draw();
					} );
					select.addClass('col-xs-12');
					select.addClass('inputBox');
				});	
				
				$('#loadingSpinner').hide();
				$('#searchResults').show();
			}
		});
		
		$('#exportButton')
		.click(
				function() {
					var table = $('#searchResults').DataTable();
					var info = table.page.info();
					var currentPage = info.page;
					var reportId= document.getElementById("reportId").value;
					var recordsPerPage = info.length;							
					if(recordsPerPage == -1) {								
						recordsPerPage = info.recordsTotal;								
					}
					window.location.href="/idap/reports/excelExport?reportId="+reportId+"&currentPage="+currentPage+"&recordsPerPage="+recordsPerPage;
				});
	});
});