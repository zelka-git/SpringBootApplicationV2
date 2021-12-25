package contracts

import static org.springframework.cloud.contract.spec.Contract.make

[
        make {
            name 'get all items'
            description 'should return all items'
            request {
                method GET()
                urlPath '/items'
            }
            response {
                status 200
            }
        },

        make {
            name 'get item by id'
            description 'should return item with given id'
            request {
                method GET()
                urlPath '/items/c3e7719a-73dd-4fdf-8038-d937defb01c4'
            }
            response {
                status 400
            }
        },

        make {
            name 'update item'
            description 'should return even when number input is even'
            request {
                method PUT()
                urlPath '/items/c3e7719a-73dd-4fdf-8038-d937defb01cf'
                body([
                        name: 'item name'
                ])
            }
            response {
                status 400
            }
        },

        make {
            name 'save item'
            description 'should save item'
            request {
                method POST()
                urlPath '/items'
                body([
                        name: 'name'
                ])
            }
            response {
                status 200
            }
        },

        make {
            name 'delete item'
            description 'should delete item'
            request {
                method DELETE()
                urlPath '/items/c3e7719a-73dd-4fdf-8038-d937defb01cf'
            }
            response {
                status 400
            }
        }

]


